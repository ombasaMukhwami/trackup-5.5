package org.traccar.broker;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.config.Config;
import org.traccar.config.Keys;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class RabbitManager implements IRabbitManager {

    private final Gson jsonConverter;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitManager.class);
    private final RabbitModelPooledObjectPolicy objectPool;
    private final RabbitOptions options;
    private final String queueName;
    private final String exchangeName;
    private final String routingKey;
    private final boolean isDurable;
    private final Map<UUID, QueuePayload> qMessages;
    private boolean inProgress = false;
    private static final int INSERT_COUNT = 10000;

    @Inject
    public RabbitManager(Config config, ObjectMapper objectMapper) {
        jsonConverter = new Gson();
        this.objectMapper = objectMapper;
        queueName = config.getString(Keys.RABBITMQ_QUEUE);
        exchangeName = config.getString(Keys.RABBITMQ_EXCHANGE);
        routingKey = config.getString(Keys.RABBITMQ_ROUTING_KEY);
        isDurable = config.getBoolean(Keys.RABBITMQ_DURABLE);

        options = new RabbitOptions(
                config.getString(Keys.RABBITMQ_HOSTNAME),
                config.getString(Keys.RABBITMQ_USERNAME),
                config.getString(Keys.RABBITMQ_PASSWORD),
                config.getString(Keys.RABBITMQ_VIRTUAL_HOST),
                config.getInteger(Keys.RABBITMQ_PORT));

        qMessages = new ConcurrentHashMap<>();
        objectPool = new RabbitModelPooledObjectPolicy(options);
        createChannels();
        scheduleQueueing();
    }

    private void scheduleQueueing() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                startSendingBulkMessages();
            }
        }, 0, 10);
    }

    private void startSendingBulkMessages() {
        if (inProgress || qMessages.size() == 0) {
            return;
        }
        inProgress = true;
        List<QueuePayload> lst = new ArrayList(qMessages.values().stream().limit(INSERT_COUNT).collect(Collectors.toList()));
        if (publish(lst)) {
            for (QueuePayload payload : lst) {
                qMessages.remove(payload.getSerialNo());
            }
        }

        inProgress = false;
    }

    private <T> boolean publish(T payload) {
        boolean isSuccess = true;
        String msg = jsonConverter.toJson(payload);
        Channel channel = objectPool.get();
        if (channel == null) {
            isSuccess = false;
        } else {
            try {
                channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
            } catch (Exception e) {
                LOGGER.error("Failed to send data to RabbitMq checkQMessages.", e);
                isSuccess = false;
            } finally {
                objectPool.pool(channel);
            }
        }
        return isSuccess;
    }


    @Override
    public <T> void queue(T message) {
        QueuePayload payload = new QueuePayload(UUID.randomUUID(), message);
        qMessages.put(payload.getSerialNo(), payload);
    }

    @Override
    public <T> boolean sendMessage(T message) {
        if (!publish(message)) {
            queue(message);
            return false;
        }

        return true;
    }

    @Override
    public void createChannels() {
        Channel publisher = objectPool.get();
        try {
            LOGGER.info("Initializing Broker.");
            publisher.exchangeDeclare(exchangeName, "direct");
            publisher.queueDeclare(queueName, isDurable, false, false, null);
            publisher.queueBind(queueName, exchangeName, routingKey);
            LOGGER.info("Completed Broker Initialization");
        } catch (IOException e) {
            LOGGER.error("channel creation Error", e);
        } finally {
            objectPool.pool(publisher);
        }
    }
}
