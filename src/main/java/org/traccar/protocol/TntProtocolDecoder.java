package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.Protocol;

import java.net.SocketAddress;

public class TntProtocolDecoder extends BaseProtocolDecoder {

    private final TntTextProtocolDecoder textProtocolDecoder;
    private final HuabaoBinaryProtocolDecoder binaryProtocolDecoder;
    public TntProtocolDecoder(Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new TntTextProtocolDecoder(protocol);
        binaryProtocolDecoder = new HuabaoBinaryProtocolDecoder(protocol);
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        boolean isBinary = HuabaoFrameDecoder.isBinary(buf);
        if (isBinary) {
            return binaryProtocolDecoder.decode(channel, remoteAddress, msg);
        }
        return textProtocolDecoder.decode(channel, remoteAddress, msg);
    }

}
