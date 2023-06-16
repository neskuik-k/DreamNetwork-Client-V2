package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.Install;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.List;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.Update;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;

public class AddonCommand extends Command {
    public AddonCommand(String name) {
        super(name);
        setCompletorValue("addon",getBaseColor()+"addon");
       // addSubCommand("install", new Install(this));
     //   addSubCommand("update", new Update(this));
        //addSubCommand("list", new List(this));

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.addon.titleUsage"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.installUsage"),"install","[" + nameText + "]");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.updateUsage"), "update", "[" + nameText + "/all]");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.listInstalledUsage"),"list","installed");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.listOfficialsUsage"),"list","officials");
    }

    @Override
    public String getBaseColor() {
        return Colors.PURPLE_BOLD;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("droplet");
    }

}
