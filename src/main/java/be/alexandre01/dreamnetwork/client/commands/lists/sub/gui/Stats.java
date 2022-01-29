package be.alexandre01.dreamnetwork.client.commands.lists.sub.gui;

import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import lombok.NonNull;

import java.io.IOException;

public class Stats extends SubCommandCompletor implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        Console.setActualConsole("m:stats");
        return true;
    }


}
