package org.traccar.broker;


public interface IRabbitManager {
    <T> boolean sendMessage(T message);
    void createChannels();
    <T> void queue(T message);
}
