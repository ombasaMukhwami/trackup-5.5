/*
 * Copyright 2015 - 2020 Anton Tananaev (anton@traccar.org)
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
package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.traccar.BaseFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HuabaoFrameDecoder extends BaseFrameDecoder {

    private static final int MINIMUM_LENGTH = 2;
    private static final Set<String> BINARY_HEADERS = new HashSet<>(
            Arrays.asList("(", "~"));

    public static boolean isBinary(ByteBuf buf) {
        String header = buf.toString(buf.readerIndex(), 1, StandardCharsets.US_ASCII);
        return BINARY_HEADERS.contains(header);
    }
    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        if (buf.readableBytes() < MINIMUM_LENGTH) {
            return null;
        }

        if (buf.getByte(buf.readerIndex()) == '(') {

            int index = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) ')');
            if (index >= 0) {
                return buf.readRetainedSlice(index + 1);
            }

        } else {

            int index = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) 0x7e);
            if (index >= 0) {
                ByteBuf result = Unpooled.buffer(index + 1 - buf.readerIndex());

                while (buf.readerIndex() <= index) {
                    int b = buf.readUnsignedByte();
                    if (b == 0x7d) {
                        int ext = buf.readUnsignedByte();
                        if (ext == 0x01) {
                            result.writeByte(0x7d);
                        } else if (ext == 0x02) {
                            result.writeByte(0x7e);
                        }
                    } else {
                        result.writeByte(b);
                    }
                }

                return result;
            }

        }

        return buf.readRetainedSlice(buf.readableBytes());
    }

}
