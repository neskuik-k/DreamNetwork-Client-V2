package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.Console;

import static be.alexandre01.dreamnetwork.core.console.jline.completors.CustomTreeCompleter.node;


public class ClearCommand extends Command {

    public ClearCommand(String clear) {
        super(clear);
        NodeBuilder nodeBuilder = new NodeBuilder(NodeBuilder.create("clear"));
       // setCompletion(node("clear"));
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
