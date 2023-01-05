package org.traccar.broker;

import javax.inject.Inject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.config.Config;
import org.traccar.config.Keys;


public class RabbitMqSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSender.class);
    private final   String hostName;
    private final String username;
    private final String password;
    private final String queueName;
    private final String exchangeName;
    private final String routingKey;
    private final boolean isDurable;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    @Inject
    public RabbitMqSender(Config config) {
        hostName = config.getString(Keys.RABBITMQ_HOSTNAME);
        username = config.getString(Keys.RABBITMQ_USERNAME);
        password = config.getString(Keys.RABBITMQ_PASSWORD);
        queueName = config.getString(Keys.RABBITMQ_QUEUE);
        exchangeName = config.getString(Keys.RABBITMQ_EXCHANGE);
        routingKey = config.getString(Keys.RABBITMQ_ROUTING_KEY);
        isDurable = config.getBoolean(Keys.RABBITMQ_DURABLE);
        setupRabbitMq();
    }

    private void setupRabbitMq() {
        try {
            factory = new ConnectionFactory();
            factory.setHost(hostName);
            factory.setPassword(password);
            factory.setUsername(username);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "topic", isDurable);
            channel.queueDeclare(queueName, isDurable, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
        } catch (Exception e) {
            LOGGER.warn("Error Initializing RabbitMq", e);
        }
    }

    public  boolean sendMessage(String message) {
       byte[] data = message.getBytes();
        try {
            channel.basicPublish(exchangeName, routingKey, null, data);
            return true;
        } catch (Exception e) {
            try {
                factory = new ConnectionFactory();
                factory.setHost(hostName);
                factory.setPassword(password);
                factory.setUsername(username);
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.basicPublish(exchangeName, routingKey, null, data);
                channel.close();
                connection.close();
                return true;
            } catch (Exception ex) {
                LOGGER.warn("Failed to send data to RabbitMq", ex);
                return false;
            }
        }
    }
}
