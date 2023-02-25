package be.alexandre01.dreamnetwork.core.commands.lists;


import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;


import java.util.logging.Level;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);
        NodeBuilder nodeBuilder = new NodeBuilder(NodeBuilder.create("help"));
        //setCompletion(node("help"));
        //setCompletions(new StringsCompleter("help"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                Console.print(Chalk.on("Lists of commands for help:").green().bold().underline());
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                Console.print(Chalk.on("How to manage a service ").underline());
                Console.print("service:");
                Console.print("service add proxy [name] | add a proxy ");
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
                Console.print(Chalk.on("How to start or stop a server: ").underline());
                Console.print("service start server [name] | start a server ");
                Console.print("service start proxy [name] | start a proxy ");
                Console.debugPrint("");
                Console.print("service stop server [name] | stop a server ");
                Console.print("service stop proxy [name] | stop a proxy ");
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to connect to the console: ").underline());
                Console.print("service screen server [name] | connect to the server's console ");
                Console.print("service screen proxy [name] | connect to the proxy's console ");

                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to remove server: ").underline());
                Console.print("service remove server [name] | remove a server ");
                Console.print("service remove proxy [name] | remove a proxy ");
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                return true;
            }
        };
    }

}
