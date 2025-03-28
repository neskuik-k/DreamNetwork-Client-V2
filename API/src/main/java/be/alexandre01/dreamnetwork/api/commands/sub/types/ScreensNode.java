package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import org.jline.builtins.Completers;

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
                if(service == null || service.getExecutor() == null){
                    return iScreen.getScreenName();
                }
                if(service.getExecutor().isProxy()){
                    color = Colors.RED;
                }else {
                    color = Colors.CYAN;
                }
                String name = iScreen.getScreenName();
                StringBuilder sb = new StringBuilder();
                sb.append(color);
                if(name.contains("/")){
                    String[] split = name.split("/");
                    sb.append(split[0]).append(Colors.YELLOW_BOLD).append("/").append(Colors.WHITE_BOLD_BRIGHT).append(split[1]);
                }
                if(name.contains("-")){
                    String[] split = name.split("-");
                    sb.append(split[0]).append(Colors.YELLOW_BOLD).append("-").append(Colors.WHITE_BOLD_BRIGHT).append(split[1]);
                }else{
                    sb.append(name);
                }
                return sb.toString();
            }).toArray(String[]::new);
            if(values.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }

            //System.out.println("Setting "+ Arrays.toString(values));
            return values;
        });
    }
}
