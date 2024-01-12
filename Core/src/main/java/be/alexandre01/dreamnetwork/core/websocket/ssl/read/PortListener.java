package be.alexandre01.dreamnetwork.core.websocket.ssl.read;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class PortListener {

    public static void main(String[] args) throws IOException {
        String prop = System.getProperty("jna.library.path");
        if (prop == null || prop.isEmpty()) {
            prop = "C:/Windows/System32/Npcap";
        } else {
            prop += ";C:/Windows/System32/Npcap";
        }
        System.setProperty("jna.library.path", prop);
        withNetty();
    }

    public static void withNetty(){

            String host = "localhost";
            int port = 25565;

            EventLoopGroup group = new NioEventLoopGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // Add your custom handlers here if needed
                            }
                        });

                // Start the client
                Channel channel = bootstrap.connect(host, port).sync().channel();

                System.out.println("The connection was established to " + channel.remoteAddress());

                // Add your logic here for interacting with the channel if needed

                // Close the channel
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
    }
}