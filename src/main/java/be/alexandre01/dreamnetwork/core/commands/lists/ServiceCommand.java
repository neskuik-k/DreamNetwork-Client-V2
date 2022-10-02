package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.*;


public class ServiceCommand extends Command {
    public ServiceCommand(String name) {
        super(name);
        addSubCommand("add",new Add());
        addSubCommand("stop",new Stop());
        addSubCommand("remove",new Remove());
        addSubCommand("start",new Start());
        addSubCommand("install",new Install());
        addSubCommand("screen",new Screen());
        addSubCommand("list",new List());
        addSubCommand("kill",new Kill());

        getHelpBuilder().setTitleUsage("How to configurate a service");
        getHelpBuilder().setCmdUsage("add a server","add","server/proxy","[name]");
        getHelpBuilder().setCmdUsage("install a server","install","server/proxy","[name]", "[Ver.]");
        getHelpBuilder().setTitleUsage("How to run a server");
        getHelpBuilder().setCmdUsage("start a server","start","server/proxy","[name]");
        getHelpBuilder().setTitleUsage("How to stop server");
        getHelpBuilder().setCmdUsage("stop a server","stop","server/proxy","[name]");
        getHelpBuilder().setTitleUsage("How to remove/delete a server");
        getHelpBuilder().setCmdUsage("remove a server","remove","server/proxy","[name]");
        getHelpBuilder().setTitleUsage("List all your servers");
        getHelpBuilder().setCmdUsage("list your servers","list");

    }

}
