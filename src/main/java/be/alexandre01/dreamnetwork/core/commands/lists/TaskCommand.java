package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.tasks.Global;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


public class TaskCommand extends Command {
    public TaskCommand(String name) {
        super(name);
        setCompletorValue("task",getBaseColor()+"task");
        addSubCommand("global",new Global(this));
        //addSubCommand("edit",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.Edit(this));
       // addSubCommand("remove",new Remove(this));
      //  addSubCommand("list",new be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles.List(this));

        String nameText = Console.getFromLang("name");
        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.bundle.titleUsage"));
        getHelpBuilder().setCmdUsage("create a deploy","(create","server/proxy","[" + nameText + "])");
        getHelpBuilder().setCmdUsage("remove a deploy","remove","[" + nameText + "]");
        getHelpBuilder().setCmdUsage("edit a deploy","edit","[" + nameText + "]", "[" + Console.getFromLang("option") + "]");
    }

    @Override
    public String getBaseColor() {
        return Colors.CYAN_BOLD;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("envelope_with_arrow");
    }
}
