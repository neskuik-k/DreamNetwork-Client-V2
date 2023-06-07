package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Create;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Remove;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


public class BundlesCommand extends Command {
    public BundlesCommand(String name) {
        super(name);
        setCompletorValue("bundle",getBaseColor()+"bundle");
        addSubCommand("create",new Create(this));
        addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit(this));
        addSubCommand("remove",new Remove(this));
        addSubCommand("list",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.List(this));

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.bundle.titleUsage"));
        getHelpBuilder().setCmdUsage("create a bundle","(create","server/proxy","[" + nameText + "])");
        getHelpBuilder().setCmdUsage("remove a bundle","remove","[" + nameText + "]");
        getHelpBuilder().setCmdUsage("edit a bundle","edit","[" + nameText + "]", "[" + Console.getFromLang("option") + "]");
    }

    @Override
    public String getBaseColor() {
        return Colors.PURPLE_BOLD_BRIGHT;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("package");
    }
}
