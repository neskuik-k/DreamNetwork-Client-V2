package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

public class Remove extends SubCommand {
    private UserManager userManager;

    public Remove(Command command, UserManager userManager) {
        super(command);
        this.userManager = userManager;
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if (args.length < 1) {
            Console.print("Usage: remove <username>");
            return false;
        }
        String username = args[0];
        userManager.removeUser(username);
        Console.print("User " + username + " removed.");
        return true;
    }
}