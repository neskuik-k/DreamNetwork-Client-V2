package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.console.Console;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:31
*/
public class WebSocketServerInitializer implements Runnable {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final int port;
    private final String host;

    public WebSocketServerInitializer(EventLoopGroup bossGroup, EventLoopGroup workerGroup, int port, String host) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.port = port;
        this.host = host;
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
                .childHandler(new HTTPInitializer());
        Channel ch = b.bind(port).sync().channel();
        ch.closeFuture().sync();
        } catch (InterruptedException e) {
            Console.bug(e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
