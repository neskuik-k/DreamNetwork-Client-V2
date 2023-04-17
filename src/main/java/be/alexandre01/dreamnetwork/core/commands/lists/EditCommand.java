package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.edit.JVM;
import be.alexandre01.dreamnetwork.core.console.Console;


public class EditCommand extends Command {

    public EditCommand(String edit) {
        super(edit);
        addSubCommand("jvm",new JVM());


        getHelpBuilder().setTitleUsage(Console.getFromLang("commands.edit.titleUsage"));
        getHelpBuilder().setCmdUsage("add a JVM","jvm","set","[" + Console.getFromLang("name") + "]","[" + Console.getFromLang("path") + "]");
        getHelpBuilder().setCmdUsage("remove a JVM","jvm","remove","[" + Console.getFromLang("name") + "]");
        getHelpBuilder().setCmdUsage("list of JVMs" ,"jvm","list");
    }
}
