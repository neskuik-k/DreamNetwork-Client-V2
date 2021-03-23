package be.alexandre01.dreamnetwork.client.commands.lists;



import be.alexandre01.dreamnetwork.client.Config;
import be.alexandre01.dreamnetwork.client.commands.CommandsExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;

import java.util.logging.Level;

public class Remove implements CommandsExecutor {
    @Override
    public boolean onCommand(String[] args) {
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
                    Console.print(Chalk.on("[!] remove server [name] => remove a server ").red(), Level.INFO);
                    Console.print(Chalk.on("[!] remove proxy [name] => remove a server ").red(), Level.INFO);
                }
            }else {
                Console.print(Chalk.on("[!] remove server [name] => remove a server").red(), Level.INFO);
            }
            return true;
        }
        return false;
    }
}
