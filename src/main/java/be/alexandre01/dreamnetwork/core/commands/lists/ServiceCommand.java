package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.*;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;


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

        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.service.titleUsage"));
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.create"),"create","server/proxy","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.install"),"install","server/proxy","[" + LanguageManager.getMessage("name") + "]", "[Ver.]");
        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.service.howTo.run"));
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.start"),"start","server/proxy","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.service.howTo.stop"));
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.stop"),"stop","server/proxy","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.service.howTo.remove"));
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.remove"),"remove","server/proxy","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.service.howTo.list"));
        getHelpBuilder().setCmdUsage(LanguageManager.getMessage("commands.service.list"),"list");

    }

}
