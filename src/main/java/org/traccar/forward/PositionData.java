/*
 * Copyright 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.forward;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionData {

    private Position position;
    private Device device;
    private String protocol;
    private Event event;

    public PositionData() {
    }

    public PositionData(Position position, Device device) {
        this.device = device;
        this.position = position;
        this.protocol = String.valueOf(position.getProtocol());
    }

    public PositionData(Position position, Device device, Event event) {
        this.device = device;
        this.position = position;
        this.protocol = String.valueOf(position.getProtocol());
        this.event = event;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
