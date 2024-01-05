package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSessionManager;
import be.alexandre01.dreamnetwork.core.websocket.sessions.frames.OverViewFrame;

import java.util.concurrent.ScheduledExecutorService;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:41
*/
public class WebSocketRun {
    public static void main(String[] args) {
        new WebSocketServer(2352, "localhost","eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9").start();
        WebSessionManager.getInstance().onNewSession(session -> {
            session.getFrameManager().addFrame("overview", new OverViewFrame(session));
        });
    }
}
