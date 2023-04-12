package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor.Reload;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor.Set;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.*;


public class HypervisorCommand extends Command {
    public HypervisorCommand(String name) {
        super(name);
        addSubCommand("reload",new Reload());
        addSubCommand("set",new Set());


    }

}
