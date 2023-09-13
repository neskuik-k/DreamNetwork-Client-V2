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

public abstract class CoreServer extends Thread{
    @Getter protected int port;


    public CoreServer(int port) {
        this.port = port;
    }

    @Override
    public abstract void run();
}
