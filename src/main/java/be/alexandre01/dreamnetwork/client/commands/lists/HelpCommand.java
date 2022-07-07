package be.alexandre01.dreamnetwork.client.commands.lists;


import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);
        setCompletion(node("help"));
        //setCompletions(new StringsCompleter("help"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                Console.print(Chalk.on("Lists of commands for help:").green().bold().underline());
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                Console.print(Chalk.on("How to manage a service ").underline());
                Console.print("service", Level.INFO);
                Console.print("service add proxy [name] | add a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
                Console.print(Chalk.on("How to start or stop a server: ").underline());
                Console.print("service start server [name] | start a server ", Level.INFO);
                Console.print("service start proxy [name] | start a proxy ", Level.INFO);
                Console.debugPrint("");
                Console.print("service stop server [name] | stop a server ", Level.INFO);
                Console.print("service stop proxy [name] | stop a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to connect to the console: ").underline());
                Console.print("service screen server [name] | connect to the server's console ", Level.INFO);
                Console.print("service screen proxy [name] | connect to the proxy's console ", Level.INFO);

                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to remove server: ").underline());
                Console.print("service remove server [name] | remove a server ", Level.INFO);
                Console.print("service remove proxy [name] | remove a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                return true;
            }
        };
    }

}
