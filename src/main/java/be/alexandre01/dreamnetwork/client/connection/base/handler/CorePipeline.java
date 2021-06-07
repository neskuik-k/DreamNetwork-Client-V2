package be.alexandre01.dreamnetwork.client.connection.base.handler;

import be.alexandre01.dreamnetwork.client.connection.base.CoreByteDecoder;
import be.alexandre01.dreamnetwork.client.connection.base.CoreMessageEncoder;
import be.alexandre01.dreamnetwork.client.connection.test.DiscardServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CorePipeline extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder",new CoreByteDecoder());
        ch.pipeline().addLast(new CoreHandler());
        ch.pipeline().addLast("encoder",new CoreMessageEncoder());
    }
}
