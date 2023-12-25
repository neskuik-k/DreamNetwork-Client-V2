package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.core.rest.DreamRestAPI;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/12/2023 at 15:19
*/
@Getter
public class WebSessionManager {
    private final List<Consumer<WebSession>> registers = new ArrayList<>();
    private final List<Consumer<WebSession>> unregisters = new ArrayList<>();
    private final List<WebSession> sessions = new ArrayList<>();

    @Getter static WebSessionManager instance;

    public WebSessionManager() {
        instance = this;
    }

    static {
        new WebSessionManager();
    }


    public void onNewSession(Consumer<WebSession> sessionConsumer){
        registers.add(sessionConsumer);
    }

    public void onSessionClose(Consumer<WebSession> sessionConsumer){
        unregisters.add(sessionConsumer);
    }

    public void registerSession(WebSession session){
        sessions.add(session);
        registers.forEach(sessionConsumer -> sessionConsumer.accept(session));
    }

    public void unregisterSession(WebSession session){
        sessions.remove(session);
        unregisters.forEach(sessionConsumer -> sessionConsumer.accept(session));
    }
}
