package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import org.jline.builtins.Completers;

public class ScreensNode extends CustomType {
    public ScreensNode(){
        type = SubCommandCompletor.Type.SERVERS;
        setCustomType(() -> {
            //Completers.TreeCompleter
            String[] screens = DNCoreAPI.getInstance().getScreenManager().getScreens().keySet().toArray(new String[0]);
            if(screens.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return screens;
        });
    }
}
