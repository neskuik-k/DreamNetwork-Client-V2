package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.Install;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.List;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.Update;
import be.alexandre01.dreamnetwork.core.console.Console;

public class AddonCommand extends Command {
    public AddonCommand(String name) {
        super(name);
        addSubCommand("install", new Install());
        addSubCommand("update", new Update());
        addSubCommand("list", new List());

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.addon.titleUsage"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.installUsage"),"install","[" + nameText + "]");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.updateUsage"), "update", "[" + nameText + "/all]");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.listInstalledUsage"),"list","installed");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.addon.listOfficialsUsage"),"list","officials");
    }
}
