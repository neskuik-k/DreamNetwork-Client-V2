package be.alexandre01.dreamnetwork.core.commands.lists.sub.gui;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.Console;
import lombok.NonNull;

public class Intro extends SubCommandCompletor implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        Console.setActualConsole("m:intro");
        return true;
    }


}
