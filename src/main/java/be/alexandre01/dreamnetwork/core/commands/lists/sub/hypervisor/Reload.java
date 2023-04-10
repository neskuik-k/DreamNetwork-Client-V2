package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import lombok.NonNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Reload extends SubCommand {
    public Reload() {
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        System.out.println(Arrays.toString(nodeClazz));
        NodeBuilder nodeBuilder = new NodeBuilder(
                create("hypervisor",
                        create("reload",
                            create("completor",create(nodeClazz)),
                                create("completors"))));
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        Console.printLang("commands.hypervisor.reloading");

        boolean b = when(sArgs -> {
            if(sArgs.length == 1){
                Console.printLang("commands.hypervisor.specifyModule");
                return true;
            }

            if(sArgs[1].equalsIgnoreCase("completors")){
                Console.printLang("commands.hypervisor.reloadingCompletors");
                reloadNode();
                Console.printLang("commands.hypervisor.reloadingCompletorsDone");
            }
            if(sArgs[1].equalsIgnoreCase("completor")){
                if(args.length == 2){
                    Console.printLang("commands.hypervisor.specifyCompletor");
                    return true;
                }

                Console.printLang("commands.hypervisor.reloadingCompletor", args[2]);

                AtomicBoolean found = new AtomicBoolean(false);
                CustomType.getCustomTypes().entries().stream().filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(args[2])).findAny().ifPresent(entry -> {
                    found.set(true);
                    Console.printLang("commands.hypervisor.reloadingClass", entry.getKey().getSimpleName());
                    CustomType.reloadAll(entry.getKey());
                });
                if(!found.get()){
                    Console.printLang("commands.hypervisor.completorNotFound");
                    return true;
                }
                Console.printLang("commands.hypervisor.reloadingCompletorDone");
            }
            return true;
        }, args,"reload","[module]","[option1]");
        return b;
    }

    public void reloadNode(){
        ConsoleReader.reloadCompleter();
    }
}
