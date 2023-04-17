package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Create;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Remove;
import be.alexandre01.dreamnetwork.core.console.Console;


public class BundlesCommand extends Command {
    public BundlesCommand(String name) {
        super(name);
        addSubCommand("create",new Create());
        addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit());
        addSubCommand("remove",new Remove());
        addSubCommand("list",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.List());

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.bundle.titleUsage"));
        getHelpBuilder().setCmdUsage("create a bundle","create","server/proxy","[" + nameText + "]");
        getHelpBuilder().setCmdUsage("remove a bundle","remove","[" + nameText + "]");
        getHelpBuilder().setCmdUsage("edit a bundle","edit","[" + nameText + "]", "[" + Console.getFromLang("option") + "]");
    }

}
