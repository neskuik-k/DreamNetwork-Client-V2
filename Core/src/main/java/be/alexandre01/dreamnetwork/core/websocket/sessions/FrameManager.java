package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;


@Getter
public class FrameManager {
    WebSession session;
    Multimap<String,Frame> frames = ArrayListMultimap.create();
    private String currentFrame = "overview";
    public FrameManager(WebSession session){
        this.session = session;
    }

    public void addFrame(String frameName,Frame frame){
        frames.put(frameName,frame);
    }
    public void removeAllFrames(String frameName){
        frames.removeAll(frameName);
    }



    public void removeFrame(String frameName,Frame frame){
        frames.remove(frameName,frame);
    }

    public void handle(String frameName,WebMessage webMessage){
        if(frames.containsKey(frameName)){
            frames.get(frameName).forEach(frame -> frame.handle(webMessage));
        }
    }

    public void handleCurrentFrame(WebMessage webMessage){
        handle(currentFrame,webMessage);
    }

    public Optional<Collection<Frame>> getFrame(String frameName){
        return Optional.ofNullable(frames.get(frameName));
    }

    public interface Frame{
        void handle(WebMessage webMessage);
    }
}
