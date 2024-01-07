package be.alexandre01.dreamnetwork.core.websocket;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Setter;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:26
*/
public class WebSocketServer extends Thread{
    private int port;
    private String host;

   @Setter
   private String secretKey;

    // using netty
    public WebSocketServer(int port, String host,String token) {
        this.port = port;
        this.host = host;
        this.secretKey = token;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            new WebSocketServerInitializer(bossGroup, workerGroup, port, host,secretKey).run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
