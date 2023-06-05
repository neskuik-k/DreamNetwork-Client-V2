package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.language.Language;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import lombok.NonNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Set extends SubCommand {
    public Set(Command command) {
        super(command);
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("set",
                            create("language",create(LanguageManager.getAvailableLanguages())))));
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {

        boolean b = when(sArgs -> {
            if(sArgs.length == 1){
                Console.printLang("commands.hypervisor.specifyModule");
                return true;
            }

            if(sArgs[1].equalsIgnoreCase("language")){
                if(args.length == 2){
                    Console.printLang("commands.hypervisor.specifyLanguage");
                    return true;
                }

                if(!Arrays.asList(LanguageManager.getAvailableLanguages()).contains(args[2])){
                    Console.printLang("commands.hypervisor.languageNotFound");
                    return true;
                }
                Main.getLanguageManager().loadDifferentLanguage(args[2]);
                Main.getGlobalSettings().setLanguage(args[2]);
                Main.getGlobalSettings().save();
                Console.printLang("commands.hypervisor.languageChanged",args[2]);
            }
            return true;
        }, args,"set","[module]","[option1]");
        return b;
    }

    public void reloadNode(){
        ConsoleReader.reloadCompleter();
    }
}
