package org.traccar.forward;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.broker.RabbitManager;
import org.traccar.config.Config;


public class PositionForwarderRabbitMQ implements PositionForwarder {

    private final RabbitManager sender;

    public PositionForwarderRabbitMQ(Config config, ObjectMapper objectMapper) {
        this.sender = new RabbitManager(config, objectMapper);
    }

    @Override
    public void forward(PositionData positionData, ResultHandler resultHandler) {
        resultHandler.onResult(sender.sendMessage(positionData), null);
    }
}
