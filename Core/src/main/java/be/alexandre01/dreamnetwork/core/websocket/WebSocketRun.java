package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSessionManager;
import be.alexandre01.dreamnetwork.core.websocket.sessions.frames.OverViewFrame;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:41
*/
public class WebSocketRun {
    public static void main(String[] args) {
        new WebSocketServer(2352, "localhost").start();
        WebSessionManager.getInstance().onNewSession(session -> {
            session.getFrameManager().addFrame("overview", new OverViewFrame(session));
        });
    }
}
