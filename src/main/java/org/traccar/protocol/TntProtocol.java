package org.traccar.protocol;

import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.config.Config;
import org.traccar.model.Command;

import javax.inject.Inject;

public class TntProtocol extends BaseProtocol {

    @Inject
    public TntProtocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new HuabaoFrameDecoder());
                pipeline.addLast(new HuabaoProtocolEncoder(TntProtocol.this));
                pipeline.addLast(new HuabaoBinaryProtocolDecoder(TntProtocol.this));
            }
        });
    }

}
