package be.alexandre01.dreamnetwork.core.commands;


import be.alexandre01.dreamnetwork.api.commands.ICommand;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;

public class CommandsManager {
    public HashMap<String, ICommand> executorList;

    public CommandsManager() {
    }

    public void addCommands(ICommand cmd) {
        this.executorList.put(cmd.getName(), cmd);
    }

    public void check(String[] args) {
        boolean hasFound = false;
        if (args[0] == null)
            return;

        if (executorList.containsKey(args[0])) {
            if (executorList.get(args[0]).onCommand(args)) {
                hasFound = true;
            }
        }
        if (!hasFound) {
            Console.print(LanguageManager.getMessage("commands.notFound"), Level.WARNING);
            PrintWriter writer = null;
        }

    }
}