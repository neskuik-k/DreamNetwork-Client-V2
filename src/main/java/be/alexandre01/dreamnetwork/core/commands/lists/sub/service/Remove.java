package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import com.github.tomaslanger.chalk.Chalk;

import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;
import static org.jline.builtins.Completers.TreeCompleter.node;

public class Remove extends SubCommandCompletor implements SubCommandExecutor {
    public Remove(){
        NodeBuilder nodeBuilder = new NodeBuilder(create("service",create("remove",create("server","proxy"))));
    }
    @Override
    public boolean onSubCommand(String[] args) {
        if(args[0].equalsIgnoreCase("remove")){
            if(args.length >= 2){
                if(args[1].equalsIgnoreCase("server")||args[1].equalsIgnoreCase("proxy")){
                    String name = args[2];
                    if(Config.contains("bundles/"+args[1].toLowerCase()+"/"+name)){
                        Config.removeDir("bundles/"+args[1].toLowerCase()+"/"+name);
                        Console.print(LanguageManager.getMessage("commands.service.remove.deleted"), Level.INFO);
                        Console.print(LanguageManager.getMessage("commands.service.remove.folderDeleted"), Level.INFO);
                    }else {
                        Console.print(LanguageManager.getMessage("commands.service.remove.nonExistentServer"), Level.WARNING);
                    }


                }else {
                    Console.print(Chalk.on("[!] service remove server [" + LanguageManager.getMessage("name") + "] => " + LanguageManager.getMessage("commands.service.remove.removeServer")).red(), Level.INFO);
                    Console.print(Chalk.on("[!] service remove proxy [" + LanguageManager.getMessage("name") + "] => " + LanguageManager.getMessage("commands.service.remove.removeProxy")).red(), Level.INFO);
                }
            }else {
                Console.print(Chalk.on("[!] service remove server [" + LanguageManager.getMessage("name") + "] => " + LanguageManager.getMessage("commands.service.remove.removeServer")).red(), Level.INFO);
            }
            return true;
        }
        return false;
    }
}
