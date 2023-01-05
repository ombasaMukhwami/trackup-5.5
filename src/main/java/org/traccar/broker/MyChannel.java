package org.traccar.broker;
import com.rabbitmq.client.Channel;

public class MyChannel implements Comparable<MyChannel> {
    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    public Channel getChannel() {
        return channel;
    }

    @Override
    public int compareTo(MyChannel myChannel) {

        if (channel.getChannelNumber() < myChannel.channel.getChannelNumber()) {
            return -1;
        } else if (channel.getChannelNumber() > myChannel.channel.getChannelNumber()) {
            return 1;
        }
        return 0;
    }
}
