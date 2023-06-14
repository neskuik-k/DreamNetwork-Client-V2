package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.*;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


public class ServiceCommand extends Command {
    public ServiceCommand(String name) {
        super(name);
        setCompletorValue("service",getBaseColor()+"service");
        addSubCommand("create",new Create(this));
        addSubCommand("stop",new Stop(this));
        addSubCommand("restart",new Restart(this));
       // addSubCommand("remove",new Remove(this));
        addSubCommand("start",new Start(this));
        addSubCommand("install",new Install(this));
        addSubCommand("screen",new Screen(this));
        addSubCommand("list",new List(this));
        addSubCommand("edit",new Edit(this));
        addSubCommand("infos",new Infos(this));
        addSubCommand("kill",new Kill(this));

        String nameLang = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.service.titleUsage"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.create"),"create","server/proxy","[" + nameLang + "]");
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.install"),"install","server/proxy","[" + nameLang + "]", "[Ver.]");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.service.howTo.run"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.start"),"start","server/proxy","[" + nameLang + "]");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.service.howTo.stop"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.stop"),"stop","server/proxy","[" + nameLang + "]");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.service.howTo.remove"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.remove"),"remove","server/proxy","[" + nameLang + "]");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.service.howTo.list"));
        getHelpBuilder().setCmdUsage(Console.getFromLang("commands.service.list"),"list");

    }

    @Override
    public String getBaseColor() {
        return Colors.GREEN_BOLD;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("gear");
    }
}
