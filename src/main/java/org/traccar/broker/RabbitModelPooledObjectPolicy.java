package org.traccar.broker;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RabbitModelPooledObjectPolicy implements IPooledObjectPolicy<Channel> {

    private int failCount = 0;
    private int fetchCount = 0;
    private int channelsCreated;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitModelPooledObjectPolicy.class);
    private  ConcurrentLinkedQueue<Channel> objPool;
    private final int maxRetained;
    private Connection connection;
    private final RabbitOptions options;
    private boolean successFull = false;
    private boolean failed = false;


    public RabbitModelPooledObjectPolicy(RabbitOptions options) {
        maxRetained = 4;
        this.options = options;
        getConnection();
    }

    private void getConnection() {
        connection = null;
        successFull = false;
        try {
            connection = options.getFactory().newConnection();
            createInitialChannel();
            failCount = 0;
            successFull = true;
        } catch (Exception e) {
            failCount++;
            if (failCount > 31) {
                LOGGER.error(String.format("%d ., Sorry unable to connect to server", failCount), e);
            }
            getConnection();
        }
    }

    private void createInitialChannel() {
        objPool = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < maxRetained; i++) {
            Channel channel = create();
            if (channel != null) {
                objPool.offer(channel);
                channelsCreated++;
            }
        }

    }

    private void closeChannel(Channel obj) {
        if (obj == null) {
            return;
        }
        int channelNumber = obj.getChannelNumber();
        try {
            obj.abort();
            channelsCreated--;
            LOGGER.info("CHANNEL " + channelNumber + "CLOSED ........" + objPool.size() + "REMAINING");
        } catch (IOException e) {
            LOGGER.error("Error CLOSING channel", e);
        }
    }

    @Override
    public Channel create() {
        try {
            return connection.createChannel();
        } catch (Exception e) {
            if (!connection.isOpen()) {
                getConnection();
                if (successFull) {
                  return create();
                }
            }

            return null;
        }
    }

    @Override
    public boolean pool(Channel obj) {
        if (obj != null && obj.isOpen()) {
            if (obj.getChannelNumber() > maxRetained * 2) {
                closeChannel(obj);
            } else {
                objPool.offer(obj);
                channelsCreated++;
            }
            return true;
        } else {
            closeChannel(obj);
            return false;
        }
    }

    private Channel check() {
        Channel channel = objPool.poll();
        if (channel != null) {
            channelsCreated--;
            failed = false;
            fetchCount = 0;
            return channel;
        }
        failed = true;
        fetchCount++;
        return null;
    }


    @Override
    public Channel get() {
        synchronized (objPool) {
            Channel channel = check();
            if (failed && fetchCount > 10) {
                return create();
            }

            return channel;
        }
    }
}
