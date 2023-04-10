package be.alexandre01.dreamnetwork.core.commands.lists;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.edit.JVM;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;


public class EditCommand extends Command {

    public EditCommand(String edit) {
        super(edit);
        addSubCommand("jvm",new JVM());


        getHelpBuilder().setTitleUsage(LanguageManager.getMessage("commands.edit.titleUsage"));
        getHelpBuilder().setCmdUsage("add a JVM","jvm","set","[" + LanguageManager.getMessage("name") + "]","[" + LanguageManager.getMessage("path") + "]");
        getHelpBuilder().setCmdUsage("remove a JVM","jvm","remove","[" + LanguageManager.getMessage("name") + "]");
        getHelpBuilder().setCmdUsage("list of JVMs" ,"jvm","list");
    }
}
