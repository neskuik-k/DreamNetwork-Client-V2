package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

public class AddToServer extends SubCommand {
    private UserManager userManager;

    public AddToServer(Command command, UserManager userManager) {
        super(command);
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if (args.length < 2) {
            Console.print("Usage: addtoserver <username> <server>");
            return false;
        }
        String username = args[0];
        String server = args[1];
        User user = userManager.getUser(username);
        this.userManager = userManager;
        if (user == null) {
            Console.print("User " + username + " does not exist.");
            return false;
        }
        user.setServerAccess(server);
        userManager.saveUsers();
        Console.print("User " + username + " now has access to " + server);
        return true;
    }
}