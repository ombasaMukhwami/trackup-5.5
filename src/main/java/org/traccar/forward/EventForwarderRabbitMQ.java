package org.traccar.forward;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.broker.RabbitManager;
import org.traccar.config.Config;


public class EventForwarderRabbitMQ  implements EventForwarder {

    private final RabbitManager sender;

    public EventForwarderRabbitMQ(Config config, ObjectMapper objectMapper) {
        this.sender = new RabbitManager(config, objectMapper);
    }
    @Override
    public void forward(EventData eventData, ResultHandler resultHandler) {
        resultHandler.onResult(sender.sendMessage(eventData), null);
    }

}
