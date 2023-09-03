package be.alexandre01.dreamnetwork.core.connection.external.handler;


import be.alexandre01.dreamnetwork.core.connection.external.ExternalClient;
import be.alexandre01.dreamnetwork.core.connection.external.communication.ExternalDecoder;
import be.alexandre01.dreamnetwork.core.connection.external.communication.ExternalEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ExternalClientPipeline extends ChannelInitializer<SocketChannel> {
    private final ExternalClient externalClient;

    public ExternalClientPipeline(ExternalClient externalClient){

        this.externalClient = externalClient;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("Decoder",new ExternalDecoder());
        ch.pipeline().addLast(new ExternalClientHandler(externalClient));
        ch.pipeline().addLast("Encoder",new ExternalEncoder());
    }
}
