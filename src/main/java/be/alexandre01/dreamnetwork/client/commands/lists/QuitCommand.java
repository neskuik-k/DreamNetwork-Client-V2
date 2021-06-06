package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import jline.console.completer.StringsCompleter;

public class QuitCommand extends Command {
    public QuitCommand(String quit) {
        super(quit);
        setCompletions(new StringsCompleter("quit"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                System.exit(0);
                return true;
            }
        };
    }
}
