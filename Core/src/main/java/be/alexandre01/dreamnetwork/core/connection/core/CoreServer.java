package be.alexandre01.dreamnetwork.core.connection.core;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CorePipeline;
import be.alexandre01.dreamnetwork.api.utils.sockets.PortUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreServer extends Thread{
    @Getter private int port;


    public CoreServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        Console console = Console.getConsole("m:default");
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            Logger.getLogger("io.netty").setLevel(Level.OFF);
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new CorePipeline())
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            boolean isAvailable = PortUtils.isAvailable(port,true);
            int defaultPort = port;
            while (!PortUtils.isAvailable(port,true)){
                port++;
            }

            /*if(!PortUtils.isAvailable(port,true)){
                Console.printLang("connection.core.portNotAvailable", port);
                System.exit(0);
            }*/
            if(!isAvailable){
                System.out.println(Console.getFromLang("connection.core.usePortInsteadOfNonAvailable", defaultPort, port));
                System.out.println(Console.getFromLang("connection.core.networkMayNotWorkCorrectly"));
            }

            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("DreamNetwork Server closed");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 14520;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new CoreServer(port).run();
    }
}
