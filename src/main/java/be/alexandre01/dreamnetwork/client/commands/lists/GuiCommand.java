package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.gui.Stats;


public class GuiCommand extends Command {

    public GuiCommand(String gui) {
        super(gui);
        addSubCommand("stats",new Stats());


        getHelpBuilder().setTitleUsage("How to view gui");
        getHelpBuilder().setCmdUsage("stats");
    }
}
