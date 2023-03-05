package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;

import static org.jline.builtins.Completers.TreeCompleter.node;


public class QuitCommand extends Command {
    public QuitCommand(String quit) {
        super(quit);
        NodeBuilder nodeBuilder = new NodeBuilder(NodeBuilder.create("quit"));
        //setCompletion(node("quit"));
        //setCompletions(new StringsCompleter("quit"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                System.exit(0);
                return true;
            }
        };
    }
}
