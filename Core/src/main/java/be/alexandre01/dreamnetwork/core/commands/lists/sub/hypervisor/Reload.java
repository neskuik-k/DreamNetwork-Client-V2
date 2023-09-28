package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.service.JVMProfiles;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Reload extends SubCommand {
    public Reload(Command command) {
        super(command);
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("reload",
                            create("services"),
                                create("tasks"),
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
            if(sArgs[1].equalsIgnoreCase("services")){
                System.out.println("Reloading services");
                for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().jvmExecutors) {
                    IStartupConfig config = jvmExecutor.getStartupConfig();
                    config.saveFile();

                    jvmExecutor.getJvmProfiles().ifPresent(iProfiles -> {
                        if(iProfiles instanceof JVMProfiles){
                            ((JVMProfiles)iProfiles).loading(iProfiles.getFile());
                        }
                    });
                }
            }
            if(sArgs[1].equalsIgnoreCase("tasks")){
                System.out.println("Reloading tasks");
                Core.getInstance().getGlobalTasks().loading();
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
        IConsoleReader.reloadCompleters();
    }
}
