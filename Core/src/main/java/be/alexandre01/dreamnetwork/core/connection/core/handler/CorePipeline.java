package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.CoreByteDecoder;
import be.alexandre01.dreamnetwork.core.connection.core.CoreMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

public class CorePipeline extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder",new CoreByteDecoder());
        ch.pipeline().addLast("encoder",new CoreMessageEncoder());
        CoreHandler coreHandler = new CoreHandler();
        Core.getInstance().setCoreHandler(coreHandler);
        ch.pipeline().addLast(coreHandler);
    }
}
