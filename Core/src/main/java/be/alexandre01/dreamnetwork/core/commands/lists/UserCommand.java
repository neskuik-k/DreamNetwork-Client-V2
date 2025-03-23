package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.user.*;

public class UserCommand extends Command {
    private UserManager userManager;

    public UserCommand(String name) {
        super(name);
        userManager = new UserManager();
        setCompletorValue("user", getBaseColor() + "service");
        addSubCommand("add", new Add(this, userManager));
        addSubCommand("create", new Create(this, userManager));
        addSubCommand("remove", new Remove(this, userManager));
        addSubCommand("addtoserver", new AddToServer(this, userManager));
        addSubCommand("removetoserver", new RemoveToServer(this, userManager));
        addSubCommand("connect", new Connection(this, userManager));
    }
}