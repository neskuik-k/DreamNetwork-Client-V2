package be.alexandre01.dreamnetwork.client.commands.lists;

import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.edit.JVM;
import be.alexandre01.dreamnetwork.client.commands.lists.sub.service.*;
import be.alexandre01.dreamnetwork.client.console.Console;

import static org.jline.builtins.Completers.TreeCompleter.node;


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
