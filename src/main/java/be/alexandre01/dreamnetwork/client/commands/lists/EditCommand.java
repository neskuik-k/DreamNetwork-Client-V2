package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.edit.JVM;


public class EditCommand extends Command {

    public EditCommand(String edit) {
        super(edit);
        addSubCommand("jvm",new JVM());


        getHelpBuilder().setTitleUsage("How to edit servers configuration");
        getHelpBuilder().setCmdUsage("add a JVM","jvm","set","[name]","[Path]");
        getHelpBuilder().setCmdUsage("remove a JVM","jvm","remove","[name]");
        getHelpBuilder().setCmdUsage("list of JVMs" ,"jvm","list");
    }
}
