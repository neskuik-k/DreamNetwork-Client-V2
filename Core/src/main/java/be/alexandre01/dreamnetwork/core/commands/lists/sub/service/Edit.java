package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.*;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.RamNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.utils.clients.RamArgumentsChecker;
import lombok.NonNull;

import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Edit extends SubCommand {
    NodeBuilder n;
    public Edit(Command command) {
        super(command);
         n = new NodeBuilder(
                create(value,
                        create("edit",create(new BundlesNode(true,true,false),
                                create("min-ram",create("set",create(new RamNode(0)))),
                                create("max-ram",create("set",create(new RamNode(0)))),
                                create("port",create("set")),
                                create("javaver",create("set")),
                                create("mods",create("set")),
                                create("deploy",create("add","remove"))))));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(!when(sArgs -> {
            Optional<IJVMExecutor> execOpt = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(args[1]);
            if(!execOpt.isPresent()){
                System.out.println("Cannot find executor");
                return false;
            }
            IJVMExecutor jvmExecutor = execOpt.get();

            if(args[3].equalsIgnoreCase("set")){
                switch (args[2]){
                    case "min-ram":
                        if(RamArgumentsChecker.check(args[4])){
                            jvmExecutor.getConfig().setXms(args[4]);
                            jvmExecutor.getStartupConfig().updateConfigFile();
                        }
                        break;
                    case "max-ram":
                        if(RamArgumentsChecker.check(args[4])){
                            jvmExecutor.getConfig().setXmx(args[4]);
                            jvmExecutor.getStartupConfig().updateConfigFile();
                        }
                        break;
                    case "mode":
                        if(args[4].equalsIgnoreCase("DYNAMIC") || args[4].equalsIgnoreCase("STATIC")){
                            jvmExecutor.getConfig().setType(IJVMExecutor.Mods.valueOf(args[4]));
                            jvmExecutor.getStartupConfig().updateConfigFile();
                        }else{
                            System.out.println("Cannot parse mode");
                            return false;
                        }
                        break;

                    case "port":
                        try {
                            jvmExecutor.getConfig().setPort(Integer.parseInt(args[4]));
                            jvmExecutor.getStartupConfig().updateConfigFile();
                        }catch (Exception e){
                            System.out.println("Cannot parse port");
                            return false;
                        }
                        break;
                    case "javaver":
                        try {
                            if(!Core.getInstance().getJavaIndex().containsKey(args[4])){
                                System.out.println("Cannot find the targetted java version");
                                return false;
                            }
                            jvmExecutor.getConfig().setJavaVersion(args[4]);
                            jvmExecutor.getStartupConfig().updateConfigFile();
                        }catch (Exception e){
                            System.out.println("Cannot parse java version");
                            return false;
                        }
                        break;
                }
                return true;
            }

            if(args[2].equalsIgnoreCase("deploy")){
                if(args[3].equalsIgnoreCase("add")){
                    if(args.length == 5){
                       /* if(!Core.getInstance().containsKey(args[4])){
                            System.out.println("Cannot find the targetted deploy");
                            return false;
                        }*/

                        jvmExecutor.getConfig().getDeployers().add(args[4]);
                        jvmExecutor.getStartupConfig().updateConfigFile();
                        return true;
                    }
                    System.out.println("Please specify a deploy");

                    return false;
                }
                if(args[3].equalsIgnoreCase("remove")){
                    if(args.length == 5){
                        /*if(!Core.getInstance().containsKey(args[4])){
                            System.out.println("Cannot find the targetted deploy");
                            return false;
                        }*/
                        jvmExecutor.getStartupConfig().getDeployers().remove(args[4]);
                        jvmExecutor.getStartupConfig().updateConfigFile();
                        return true;
                    }
                    System.out.println("Please specify a deploy");
                    return false;
                }
            }

            return true;
        },args,"edit","data", "method","value")){

            return true;
        }
        System.out.println(Colors.GREEN+" You successfully edited the data"+Colors.RESET);
        return true;
    }
}
