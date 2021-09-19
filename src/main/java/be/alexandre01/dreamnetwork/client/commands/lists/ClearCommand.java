package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;

import static org.jline.builtins.Completers.TreeCompleter.node;


public class ClearCommand extends Command {

    public ClearCommand(String clear) {
        super(clear);
        setCompletion(node("clear"));
            //setCompletions(new StringsCompleter("clear"));

            commandExecutor = new CommandExecutor() {
                @Override
                public boolean execute(String[] args) {
                    Console.clearConsole();
                    return true;
                }
            };

    }
}
