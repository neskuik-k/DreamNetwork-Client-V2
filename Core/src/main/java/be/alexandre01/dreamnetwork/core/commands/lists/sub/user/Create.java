package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

public class Create extends SubCommand {
    private UserManager userManager;

    public Create(Command command, UserManager userManager) {
        super(command);
        this.userManager = userManager;
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if (args.length < 2) {
            Console.print("Usage: create <username> <password>");
            return false;
        }
        String username = args[0];
        String password = args[1];
        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = new User(username, passwordHash, "");
        userManager.addUser(user);
        Console.print("User " + username + " created.");
        return true;
    }
}