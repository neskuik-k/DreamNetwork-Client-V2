package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import lombok.Getter;
@Getter
public abstract class FrameTester {
    FrameAbstraction frame;
    public FrameTester(FrameAbstraction frameAbstraction) {
        this.frame = frameAbstraction;
    }

    public WebSession getSession(){
        return frame.getSession();
    }

    public void removeFrame(){
        frame.removeFrame();
    }

    public abstract void testSend();

    public abstract void testReceive(WebMessage webMessage);

    public abstract void testLeave();
    public abstract void testEnter();
}
