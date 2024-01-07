package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.core.connection.core.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CorePipeline extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ByteCounting byteCounting = new ByteCounting();
        ByteCountingInboundHandler byteCountingInboundHandler = new ByteCountingInboundHandler(byteCounting);
        ByteCountingOutboundHandler byteCountingOutboundHandler = new ByteCountingOutboundHandler(byteCounting);
        ch.pipeline().addLast("byteCounterIn", byteCountingInboundHandler);
        ch.pipeline().addLast("byteCounterOut", byteCountingOutboundHandler);
        ch.pipeline().addLast("decoder",new CoreByteDecoder());
        ch.pipeline().addLast("encoder",new CoreMessageEncoder());
        CoreHandler coreHandler = new CoreHandler(byteCountingInboundHandler);
        ch.pipeline().addLast(coreHandler);
    }
}
