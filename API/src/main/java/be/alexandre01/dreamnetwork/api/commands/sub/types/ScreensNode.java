package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import org.jline.builtins.Completers;

import java.util.Arrays;
import java.util.Collection;

public class ScreensNode extends CustomType {
    public ScreensNode(){
        type = SubCommandCompletor.Type.SERVERS;
        setCustomType(() -> {
            //Completers.TreeCompleter
            String[] values;
            Collection<IScreen> screens = DNCoreAPI.getInstance().getScreenManager().getScreens().values();
            values = screens.stream().map(iScreen -> {
                IService service = iScreen.getService();
                String color;
                if(service == null || service.getJvmExecutor() == null){
                    return iScreen.getScreenName();
                }
                if(service.getJvmExecutor().isProxy()){
                    color = Colors.RED;
                }else {
                    color = Colors.CYAN;
                }
                String bundle = service.getJvmExecutor().getBundleData().getName();
                return color+bundle+ Colors.YELLOW_BOLD+"/"+Colors.WHITE_BOLD_BRIGHT+service.getJvmExecutor().getName()+Colors.YELLOW_BOLD+"-"+Colors.WHITE_BOLD_BRIGHT+iScreen.getScreenId();
            }).toArray(String[]::new);
            if(values.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }

            //System.out.println("Setting "+ Arrays.toString(values));
            return values;
        });
    }
}
