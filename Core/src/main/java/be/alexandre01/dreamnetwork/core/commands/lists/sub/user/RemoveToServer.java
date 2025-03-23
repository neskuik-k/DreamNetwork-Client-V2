package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

public class RemoveToServer extends SubCommand {
    private UserManager userManager;

    public RemoveToServer(Command command, UserManager userManager) {
        super(command);
        this.userManager = userManager;
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if (args.length < 1) {
            Console.print("Usage: removetoserver <username>");
            return false;
        }
        String username = args[0];
        User user = userManager.getUser(username);
        if (user == null) {
            Console.print("User " + username + " does not exist.");
            return false;
        }
        user.setServerAccess("");
        userManager.saveUsers();
        Console.print("User " + username + " access to server removed.");
        return true;
    }
}