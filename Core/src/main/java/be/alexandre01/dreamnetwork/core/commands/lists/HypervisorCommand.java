package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor.*;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;


public class HypervisorCommand extends Command {
    public HypervisorCommand(String name) {
        super(name);
    //    value = new Candidate("hypervisor", getBaseColor()+"hypervisor ", null, "hi",  "ah"+Console.getEmoji("cloud"), null, true);
        setCompletorValue("hypervisor",getBaseColor()+"hypervisor");
        addSubCommand("reload",new Reload(this));
        addSubCommand("set",new Set(this));
        addSubCommand("connect",new Connect(this));
        addSubCommand("open",new Open(this));
        addSubCommand("ssl",new Ssl(this));
    }
    @Override
    public String getBaseColor() {
        return Colors.CYAN_BOLD;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("globe_with_meridians");
    }

}
