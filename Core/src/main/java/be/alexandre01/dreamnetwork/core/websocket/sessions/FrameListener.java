package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;

import java.util.Collection;

public class FrameListener implements WebSession.MessageListener {
    final WebSession session;

    public FrameListener(WebSession session) {
        this.session = session;
        session.onClose(() -> {
            session.getFrameManager().getFrames().forEach((s, frame) -> {
                if(frame instanceof FrameAbstraction){
                    FrameAbstraction frameAbstraction = (FrameAbstraction) frame;
                    frameAbstraction.execLeave();
                }
            });
        });
    }

    @Override
    public void onRead(WebMessage message) {
        if (message.containsKey("frame")) {
            if (message.containsKey("type")) {
                String frameName = message.getString("frame");
                String type = message.getString("type");

                if (type.equalsIgnoreCase("enter")) {
                    System.out.println(session.getFrameManager().getFrame(frameName).map(Collection::size).orElse(0));
                    session.getFrameManager().getFrame(frameName).ifPresent(frames -> frames.forEach(frame -> {
                        System.out.println("Found frame !");
                        if (frame instanceof FrameAbstraction) {
                            System.out.println("Frame is instanceof FrameAbstraction !");
                            FrameAbstraction frameAbstraction = (FrameAbstraction) frame;
                            session.getFrameManager().setCurrentFrame(frameName);
                            frameAbstraction.execEnter();
                        }
                    }));
                }
                if (type.equalsIgnoreCase("leave")) {
                    session.getFrameManager().getFrame(frameName).ifPresent(frames -> frames.forEach(frame -> {
                        if (frame instanceof FrameAbstraction) {
                            FrameAbstraction frameAbstraction = (FrameAbstraction) frame;
                            session.getFrameManager().setCurrentFrame(null);
                            frameAbstraction.execLeave();
                        }
                    }));
                }
            }
        }
    }
}
