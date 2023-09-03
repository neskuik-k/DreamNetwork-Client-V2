package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

import java.io.File;

import static be.alexandre01.dreamnetwork.api.console.jline.completors.CustomTreeCompleter.node;

public class Edit extends SubCommand  {
    public Edit(Command command){
        super(command);
        setCompletion(node(value,
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
                        Console.printLang("commands.bundle.edit.dontExists", oldName);
                        return true;
                    }
                    file.renameTo(new File(Config.getPath("bundles/"+name)));

                    File info = new File(Config.getPath("bundles/"+name+"/this-info.yml"));
                   // BundleFileInfo.updateFile(info,name);
                    Console.printLang("commands.bundle.edit.renamed", oldName, name);
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
