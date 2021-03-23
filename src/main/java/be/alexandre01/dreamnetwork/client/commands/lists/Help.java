package be.alexandre01.dreamnetwork.client.commands.lists;


import be.alexandre01.dreamnetwork.client.commands.CommandsExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import com.github.tomaslanger.chalk.Chalk;

import java.util.logging.Level;

public class Help implements CommandsExecutor {

    public boolean onCommand(String[] args) {
        if(args[0].equalsIgnoreCase("help")){
            Console.print(Chalk.on("Lists of commands for help:").green().bold().underline());
            Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
            Console.print(Chalk.on("How to configurate a server: ").underline());
            Console.print("add server [name] | add a server ", Level.INFO);
            Console.print("add proxy [name] | add a proxy ", Level.INFO);
            Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
            Console.print(Chalk.on("How to start or stop a server: ").underline());
            Console.print("start server [name] | start a server ", Level.INFO);
            Console.print("start proxy [name] | start a proxy ", Level.INFO);
            Console.debugPrint("");
            Console.print("stop server [name] | stop a server ", Level.INFO);
            Console.print("stop proxy [name] | stop a proxy ", Level.INFO);
            Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

            Console.print(Chalk.on("How to connect to the console: ").underline());
            Console.print("screen server [name] | connect to the server's console ", Level.INFO);
            Console.print("screen proxy [name] | connect to the proxy's console ", Level.INFO);

            Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

            Console.print(Chalk.on("How to remove server: ").underline());
            Console.print("remove server [name] | remove a server ", Level.INFO);
            Console.print("remove proxy [name] | remove a proxy ", Level.INFO);
            Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());

            return true;
        }
        return false;

    }
}
