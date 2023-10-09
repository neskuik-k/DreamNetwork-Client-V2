package be.alexandre01.dreamnetwork.core.commands;


import be.alexandre01.dreamnetwork.api.commands.ICommand;
import be.alexandre01.dreamnetwork.api.commands.ICommandsManager;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

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

        if(!hasFound){
            List<SubCommandExecutor> subCommandExecutor = executorsList.values().stream().filter(iCommand -> iCommand.getSubCommand(args[0]) != null).map(command -> command.getSubCommand(args[0])).collect(Collectors.toList());
            if(!subCommandExecutor.isEmpty()){
                if(subCommandExecutor.size() == 1){
                    if(subCommandExecutor.get(0).onSubCommand(args)){
                        System.out.println(Colors.GREEN+"A SubCommand has been found and has been executed");
                        hasFound = true;
                    }
                    return;
                }
            }
            Console.printLang("commands.notFound", Level.WARNING);
            return;
        }

    }

}