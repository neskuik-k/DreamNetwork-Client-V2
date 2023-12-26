package be.alexandre01.dreamnetwork.core.websocket.sessions;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Getter
public abstract class FrameAbstraction implements FrameManager.Frame {
    WebSession session;
    String frameName;
    @Getter(AccessLevel.NONE) FrameTester frameTester = null;
    ScheduledExecutorService executorService;


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
        if (executorService != null) {
            executorService.shutdown();
        }
        getFrameTester().ifPresent(FrameTester::testLeave);
        onLeave();
    }

    public Optional<FrameTester> getFrameTester(){
        return Optional.ofNullable(frameTester);
    }

    public void onLeave(){}

    public void setRefreshRate(long refreshRate){
        if(executorService != null){
            executorService.shutdown();
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(this::refresh, 0, refreshRate, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void refresh(){}
}
