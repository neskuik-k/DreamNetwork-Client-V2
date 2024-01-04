package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.rest.DreamRestAPI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.Setter;

import java.security.cert.CertificateException;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:31
*/
public class WebSocketServerInitializer implements Runnable {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    @Getter private DreamRestAPI dreamRestAPI;
    @Getter private final int port;
   @Getter @Setter
   private String prefix = "wss://";
    private final String host;

    public WebSocketServerInitializer(EventLoopGroup bossGroup, EventLoopGroup workerGroup, int port, String host,String token) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.port = port;
        this.host = host;
        this.dreamRestAPI = new DreamRestAPI();
        dreamRestAPI.create();
        String refreshSocket = dreamRestAPI.checkup(token, String.valueOf(getPort()));

    }

    @Override
    public void run() {
        try {
            System.out.println("Starting websocket server on port "+port);
        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HTTPInitializer(this));
        Channel ch = b.bind(port).sync().channel();
        ch.closeFuture().sync();
        } catch (InterruptedException e) {
            Console.bug(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
