package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.service.Install;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.service.Start;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.service.Add;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.service.Remove;

public class ServiceCommand extends Command {
    public ServiceCommand(String name) {
        super(name);

        addSubCommand("add",new Add());
        addSubCommand("remove",new Remove());
        addSubCommand("start",new Start());
        addSubCommand("install",new Install());

        getHelpBuilder().setTitleUsage("How to configurate server");
        getHelpBuilder().setCmdUsage("add a server","add","server","[name]");

    }

}
