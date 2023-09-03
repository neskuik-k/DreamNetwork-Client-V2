package be.alexandre01.dreamnetwork.core.commands;


import be.alexandre01.dreamnetwork.api.commands.ICommand;
import be.alexandre01.dreamnetwork.api.commands.ICommandsManager;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;

public class CommandsManager implements ICommandsManager {
    @Getter public final HashMap<String, ICommand> executorsList;

    public CommandsManager() {
        executorsList = new HashMap<>();
    }

    @Override
    public void addCommands(ICommand cmd) {
        this.executorsList.put(cmd.getName(), cmd);
    }

    @Override
    public void check(String[] args) {
        boolean hasFound = false;
        if (args[0] == null)
            return;

        if (executorsList.containsKey(args[0])) {
            if (executorsList.get(args[0]).onCommand(args)) {
                hasFound = true;
            }
        }
        if (!hasFound) {
            Console.printLang("commands.notFound", Level.WARNING);
            PrintWriter writer = null;
        }

    }

}