package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;

import java.util.logging.Level;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Remove extends SubCommandCompletor implements SubCommandExecutor {
    public Remove(){
        setCompletion(node("service",
                node("remove",
                        node("server", "proxy"))));
    }
    @Override
    public boolean onSubCommand(String[] args) {
        if(args[0].equalsIgnoreCase("remove")){
            if(args.length >= 2){
                if(args[1].equalsIgnoreCase("server")||args[1].equalsIgnoreCase("proxy")){
                    String name = args[2];
                    if(Config.contains("template/"+args[1].toLowerCase()+"/"+name)){
                        Config.removeDir("template/"+args[1].toLowerCase()+"/"+name);
                        Console.print(Chalk.on("[V] This server has been deleted").blue(), Level.INFO);
                        Console.print(Chalk.on("The folder has been deleted in the 'template' folder ").blue(), Level.INFO);
                    }else {
                        Console.print(Chalk.on("[!] This server does not exist").red(), Level.WARNING);
                    }


                }else {
                    Console.print(Chalk.on("[!] service remove server [name] => remove a server ").red(), Level.INFO);
                    Console.print(Chalk.on("[!] service remove proxy [name] => remove a server ").red(), Level.INFO);
                }
            }else {
                Console.print(Chalk.on("[!] service remove server [name] => remove a server").red(), Level.INFO);
            }
            return true;
        }
        return false;
    }
}
