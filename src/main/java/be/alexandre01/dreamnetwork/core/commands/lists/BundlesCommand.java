package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Create;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Remove;


public class BundlesCommand extends Command {
    public BundlesCommand(String name) {
        super(name);
        addSubCommand("create",new Create());
        addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit());
        addSubCommand("remove",new Remove());

        getHelpBuilder().setTitleUsage("How to configurate your services bundles");
        getHelpBuilder().setCmdUsage("create a bundle","create","server/proxy","[name]");
        getHelpBuilder().setCmdUsage("remove a bundle","remove","[name]");
        getHelpBuilder().setCmdUsage("edit a bundle","edit","[name]", "[option]");

    }

}
