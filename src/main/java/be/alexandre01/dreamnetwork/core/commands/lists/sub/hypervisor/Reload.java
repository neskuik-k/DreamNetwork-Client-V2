package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
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
        System.out.println("Reloading commands...");

        boolean b = when(sArgs -> {
            if(sArgs.length == 1){
                System.out.println("Please specify a module to reload");
                return true;
            }

            if(sArgs[1].equalsIgnoreCase("completors")){
                System.out.println("Reloading completors...");
                reloadNode();
                System.out.println("Reloading completors done !");
            }
            if(sArgs[1].equalsIgnoreCase("completor")){
                if(args.length == 2){
                    System.out.println("Please specify a completor to reload");
                    return true;
                }

                System.out.println("Reloading completor "+args[2]+"...");

                AtomicBoolean found = new AtomicBoolean(false);
                CustomType.getCustomTypes().entries().stream().filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(args[2])).findAny().ifPresent(entry -> {
                    found.set(true);
                    System.out.println("Reloading "+entry.getKey().getSimpleName()+".class");
                    CustomType.reloadAll(entry.getKey());
                });
                if(!found.get()){
                    System.out.println("Completor not found");
                    return true;
                }
                System.out.println("Reloading completor done !");
            }
            return true;
        }, args,"reload","[module]","[option1]");
        return b;
    }

    public void reloadNode(){
        ConsoleReader.reloadCompleter();
    }
}
