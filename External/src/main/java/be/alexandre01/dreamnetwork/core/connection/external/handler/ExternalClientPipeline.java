package be.alexandre01.dreamnetwork.core.connection.external.handler;


import be.alexandre01.dreamnetwork.core.connection.external.ExternalServer;
import be.alexandre01.dreamnetwork.core.connection.external.communication.ExternalDecoder;
import be.alexandre01.dreamnetwork.core.connection.external.communication.ExternalEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ExternalClientPipeline extends ChannelInitializer<SocketChannel> {
    private final ExternalServer externalServer;

    public ExternalClientPipeline(ExternalServer externalServer){
        this.externalServer = externalServer;
        System.out.println("WOW");
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("Decoder",new ExternalDecoder());
        ch.pipeline().addLast(new ExternalClientHandler(externalServer));
        ch.pipeline().addLast("Encoder",new ExternalEncoder());
    }
}
