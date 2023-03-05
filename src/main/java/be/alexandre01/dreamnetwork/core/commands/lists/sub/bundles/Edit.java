package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.core.config.Config;
import lombok.NonNull;

import java.io.File;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Edit extends SubCommand  {
    public Edit(){
        setCompletion(node("bundles",
                node("edit")));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("edit")){
            when(sArgs -> {
                if(sArgs[2].equalsIgnoreCase("name")){
                    String name = sArgs[3];
                    String oldName = sArgs[1];

                    File file = new File(Config.getPath("bundles/"+oldName));
                    if(!file.exists()){
                        System.out.println("The bundle "+oldName+" doesn't exist");
                        return true;
                    }
                    file.renameTo(new File(Config.getPath("bundles/"+name)));

                    File info = new File(Config.getPath("bundles/"+name+"/this-info.yml"));
                   // BundleFileInfo.updateFile(info,name);
                    System.out.println("Renamed "+oldName+" to "+name);
                    return true;
                }
                if(sArgs[2].equalsIgnoreCase("type")){

                }
                return true;
            },args,"edit","[bundle]","[option]");
            return true;
        }
        return false;
    }
}
