package be.alexandre01.dreamnetwork.core.commands.lists;


import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
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
                Console.print(LanguageManager.getMessage("commands.help.list="));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                Console.print(LanguageManager.getMessage("commands.help.howTo.manageService"));
                Console.print("service:");
                Console.print("service add server [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.addA").replaceFirst("%var%", LanguageManager.getMessage("server")));
                Console.print("service add proxy [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.addA").replaceFirst("%var%", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
                Console.print(LanguageManager.getMessage("commands.help.howTo.startStopService"));
                Console.print("service start server [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.startA").replaceFirst("%var%", LanguageManager.getMessage("server")));
                Console.print("service start proxy [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.startA").replaceFirst("%var%", "proxy"));
                Console.debugPrint("");
                Console.print("service stop server [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.stopA").replaceFirst("%var%", LanguageManager.getMessage("server")));
                Console.print("service stop proxy [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.stopA").replaceFirst("%var%", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(LanguageManager.getMessage("commands.help.howTo.connectConsole"));
                Console.print("service screen server [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.connectTo").replaceFirst("%var%", LanguageManager.getMessage("server")));
                Console.print("service screen proxy [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.connectTo").replaceFirst("%var%", "proxy"));

                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(LanguageManager.getMessage("commands.help.howTo.removeService"));
                Console.print("service remove server [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.removeA").replaceFirst("%var%", LanguageManager.getMessage("server")));
                Console.print("service remove proxy [" + LanguageManager.getMessage("name") + "] | " + LanguageManager.getMessage("commands.help.removeA").replaceFirst("%var%", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                return true;
            }
        };
    }

}
