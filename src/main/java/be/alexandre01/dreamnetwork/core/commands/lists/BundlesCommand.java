package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Create;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Remove;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;


public class BundlesCommand extends Command {
    public BundlesCommand(String name) {
        super(name);
        addSubCommand("create",new Create());
        addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit());
        addSubCommand("remove",new Remove());
        addSubCommand("list",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.List());

        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.bundle.titleUsage"));
        getHelpBuilder().setCmdUsage("create a bundle","create","server/proxy","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setCmdUsage("remove a bundle","remove","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setCmdUsage("edit a bundle","edit","[" + LanguageManager.getMessage("name") + "]", "[" + LanguageManager.getMessage("option") + "]");
    }

}
