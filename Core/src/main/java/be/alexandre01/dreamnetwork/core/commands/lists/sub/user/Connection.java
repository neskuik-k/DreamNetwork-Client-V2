package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.NonNull;

public class Connection extends SubCommand {
    private UserManager userManager;

    public Connection(Command command, UserManager userManager) {
        super(command);
        this.userManager = userManager;
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if (args.length < 2) {
            Console.print("Usage: connect <username> <password>");
            return false;
        }
        String username = args[0];
        String password = args[1];
        return connect(username, password);
    }

    private boolean connect(String username, String password) {
        User user = userManager.getUser(username);
        if (user == null) {
            Console.print("User " + username + " does not exist.");
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash());
        if (result.verified) {
            Console.print("User " + username + " connected successfully.");
            return true;
        } else {
            Console.print("Invalid password for user " + username + ".");
            return false;
        }
    }
}