package be.alexandre01.dreamnetwork.client.commands.lists;


import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SpigetCommand extends Command {

    public SpigetCommand(String name) {
        super(name);
        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                Console.setActualConsole("m:spiget");
                return true;
            }
        };
    }


}
