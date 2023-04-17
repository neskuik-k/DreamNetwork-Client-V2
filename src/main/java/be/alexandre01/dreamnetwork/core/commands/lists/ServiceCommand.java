package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.*;
import be.alexandre01.dreamnetwork.core.console.Console;


public class ServiceCommand extends Command {
    public ServiceCommand(String name) {
        super(name);
        addSubCommand("create",new Create());
        addSubCommand("stop",new Stop());
        addSubCommand("remove",new Remove());
        addSubCommand("start",new Start());
        addSubCommand("install",new Install());
        addSubCommand("screen",new Screen());
        addSubCommand("list",new List());
        addSubCommand("kill",new Kill());

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

}
