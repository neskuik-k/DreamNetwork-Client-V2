package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSessionManager;
import be.alexandre01.dreamnetwork.core.websocket.sessions.frames.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
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

   @Getter static WebSocketServer instance;
   WebSocketServerInitializer webSocketServerInitializer;

    // using netty
    public WebSocketServer(int port, String host,String token) {
        instance = this;
        this.port = port;
        this.host = host;
        this.secretKey = token;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            webSocketServerInitializer = new WebSocketServerInitializer(bossGroup, workerGroup, port, host,secretKey);
            webSocketServerInitializer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(int port,String token){
        if(WebSocketServer.getInstance() != null){
            WebSocketServer.getInstance().interrupt();
        }
        new WebSocketServer(port, "localhost",token).start();
        WebSessionManager.getInstance().onNewSession(session -> {
            session.getFrameManager().addFrame("overview", new OverViewFrame(session));
            session.getFrameManager().addFrame("players", new PlayersFrame(session));
            session.getFrameManager().addFrame("executors", new ExecutorsFrame(session));
            session.getFrameManager().addFrame("services", new ServicesFrame(session));
            session.getFrameManager().addFrame("innerService", new InnerServiceFrame(session));
            session.onRead(message -> {
                session.getFrameManager().handleCurrentFrame(message);
            });
        });
    }

    @Override
    public void interrupt() {
        webSocketServerInitializer.getChannel().close();
        super.interrupt();
    }
}
