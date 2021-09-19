package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import com.github.tomaslanger.chalk.Chalk;
import org.jline.reader.impl.completer.NullCompleter;

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
                        Console.print(Chalk.on("[V] Ce serveur a été supprimé").blue(), Level.INFO);
                        Console.print(Chalk.on("Le dossier a été supprimé dans le dossier 'template' ").blue(), Level.INFO);
                    }else {
                        Console.print(Chalk.on("[!] Ce serveur n'existe pas").red(), Level.WARNING);
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
