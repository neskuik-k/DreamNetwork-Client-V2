package be.alexandre01.dreamnetwork.core.websocket.sessions;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Consumer;

@Getter
public abstract class FrameAbstraction implements FrameManager.Frame {
    WebSession session;
    String frameName;
    @Getter(AccessLevel.NONE) FrameTester frameTester = null;


    public FrameAbstraction(WebSession session, String frameName) {
        this.session = session;
        this.frameName = frameName;

        //session.getFrameManager().addFrame(frameName, this);
    }

    public void removeFrame(){
        session.getFrameManager().removeAllFrames(frameName);
    }

    public void setTester(FrameTester frameTester){
        this.frameTester = frameTester;
    }

    public void execEnter(){
        System.out.println("Exec enter !");
        getFrameTester().ifPresent(FrameTester::testEnter);
        onEnter();
    }
    public void onEnter(){}

    public void execLeave(){
        getFrameTester().ifPresent(FrameTester::testLeave);
        onLeave();
    }

    public Optional<FrameTester> getFrameTester(){
        return Optional.ofNullable(frameTester);
    }

    public void onLeave(){}

    public void setRefreshRate(int refreshRate){}
}
