package org.traccar.broker;

import java.util.UUID;

public class QueuePayload<T> {
    private final UUID serialNo;
    private final T data;
   public QueuePayload(UUID uuid, T msg) {
       serialNo = uuid;
       data = msg;
   }

   public UUID getSerialNo() {
        return serialNo;
    }

    public T getData() {
        return data;
    }
}
