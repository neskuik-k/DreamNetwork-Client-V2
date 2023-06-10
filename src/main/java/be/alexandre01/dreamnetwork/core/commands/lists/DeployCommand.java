package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.deploys.Create;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


public class DeployCommand extends Command {
    public DeployCommand(String name) {
        super(name);
        setCompletorValue("deploy",getBaseColor()+"deploy");
        addSubCommand("create",new Create(this));
        addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit(this));
       // addSubCommand("remove",new Remove(this));
        addSubCommand("list",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.List(this));

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.bundle.titleUsage"));
        getHelpBuilder().setCmdUsage("create a bundle","(create","server/proxy","[" + nameText + "])");
        getHelpBuilder().setCmdUsage("remove a bundle","remove","[" + nameText + "]");
        getHelpBuilder().setCmdUsage("edit a bundle","edit","[" + nameText + "]", "[" + Console.getFromLang("option") + "]");
    }

    @Override
    public String getBaseColor() {
        return Colors.YELLOW_BOLD;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("package");
    }
}
