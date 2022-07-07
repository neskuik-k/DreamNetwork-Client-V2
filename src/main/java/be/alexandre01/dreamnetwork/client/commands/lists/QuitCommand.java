package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;

import static org.jline.builtins.Completers.TreeCompleter.node;


public class QuitCommand extends Command {
    public QuitCommand(String quit) {
        super(quit);
        setCompletion(node("quit"));
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
