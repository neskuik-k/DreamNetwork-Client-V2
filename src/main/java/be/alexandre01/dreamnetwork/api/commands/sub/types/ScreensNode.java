package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import org.jline.builtins.Completers;

public class ScreensNode extends CustomType {
    public ScreensNode(){
        type = SubCommandCompletor.Type.SERVERS;
        setCustomType(() -> {
            //Completers.TreeCompleter
            String[] screens = ScreenManager.instance.getScreens().keySet().toArray(new String[0]);
            if(screens.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return screens;
        });
    }
}
