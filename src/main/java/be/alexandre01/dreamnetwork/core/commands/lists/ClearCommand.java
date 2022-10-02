package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.console.Console;

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
