package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class SocketIOTest {
    public static void main (String[] args) {
        Configuration config = new Configuration();
       // config.setHostname("localhost");
        config.setPort(2001);
        SocketIOServer server = new SocketIOServer(config);
        server.start();

        server.addConnectListener(socketIOClient -> {
            System.out.println("Client connected: " + socketIOClient.getSessionId());
        });

        server.addEventListener("chat", String.class, (socketIOClient, s, chatMessage) -> {
            System.out.println("Received: " + s);
            server.getBroadcastOperations().sendEvent("chat", chatMessage);
        });
    }

}
