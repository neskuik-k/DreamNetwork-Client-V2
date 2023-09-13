package be.alexandre01.dreamnetwork.core.connection.core;

import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.api.utils.sockets.PortUtils;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CorePipeline;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReactorNettyServer extends CoreServer{


    public ReactorNettyServer(int port) {
        super(port);
    }

    @Override
    public void run() {
            // Bind and start to accept incoming connections.
        GlobalSettings settings = Main.getGlobalSettings();
        LoopResources loop = LoopResources.create("event-loop", settings.getNettyBossThreads(), settings.getNettyWorkerThreads(), true);
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


                DisposableServer server =
                TcpServer.create()
                        .port(port)
                        .runOn(loop)
                        .doOnChannelInit((connectionObserver, channel, socketAddress) -> {
                            channel.pipeline().addFirst( new LoggingHandler(LogLevel.INFO));
                            channel.pipeline().addLast(new CorePipeline());
                        })
                        .handle((inbound, outbound) -> {
                            return outbound.sendString(Mono.just(new Message().set("ReactorNettyServer","test").toString()));
                        })
                        .bindNow();


                server.onDispose()
                .block();
            //ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.



            //f.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws Exception {
        int port = 14520;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new ReactorNettyServer(port).run();
    }
}
