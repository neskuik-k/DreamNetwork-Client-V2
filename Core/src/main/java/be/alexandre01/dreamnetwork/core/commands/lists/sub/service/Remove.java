package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.console.Console;
import com.github.tomaslanger.chalk.Chalk;

import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Remove extends SubCommandCompletor implements SubCommandExecutor {
    public Remove(Command command){
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(create(value,create("remove",create("server","proxy"))));
    }
    @Override
    public boolean onSubCommand(String[] args) {
        String nameLang = Console.getFromLang("name");
        if(args[0].equalsIgnoreCase("remove")){
            if(args.length >= 2){
                if(args[1].equalsIgnoreCase("server")||args[1].equalsIgnoreCase("proxy")){
                    String name = args[2];
                    if(Config.contains("bundles/"+args[1].toLowerCase()+"/"+name)){
                        Config.removeDir("bundles/"+args[1].toLowerCase()+"/"+name);
                        Console.printLang("commands.service.remove.deleted");
                        Console.printLang("commands.service.remove.folderDeleted");
                    }else {
                        Console.printLang("commands.service.remove.nonExistentServer", Level.WARNING);
                    }


                }else {
                    Console.print(Chalk.on("[!] service remove server [" + nameLang + "] => " + Console.getFromLang("commands.service.remove.removeServer")).red(), Level.INFO);
                    Console.print(Chalk.on("[!] service remove proxy [" + nameLang + "] => " + Console.getFromLang("commands.service.remove.removeProxy")).red(), Level.INFO);
                }
            }else {
                Console.print(Chalk.on("[!] service remove server [" + nameLang + "] => " + Console.getFromLang("commands.service.remove.removeServer")).red(), Level.INFO);
            }
            return true;
        }
        return false;
    }
}
