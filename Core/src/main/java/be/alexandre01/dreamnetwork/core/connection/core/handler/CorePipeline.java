package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.core.connection.core.CoreByteDecoder;
import be.alexandre01.dreamnetwork.core.connection.core.CoreMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CorePipeline extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder",new CoreByteDecoder());
        ch.pipeline().addLast("encoder",new CoreMessageEncoder());
        ch.pipeline().addLast(new CoreHandler());
    }
}
