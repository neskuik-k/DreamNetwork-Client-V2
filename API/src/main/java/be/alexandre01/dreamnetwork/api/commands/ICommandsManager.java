package be.alexandre01.dreamnetwork.api.commands;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 18:23
*/
public interface ICommandsManager {
    void addCommands(ICommand cmd);

    void check(String[] args);

    HashMap<String, ICommand> getExecutorsList();
}
