package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Infos extends SubCommand {
    NodeBuilder n;
    public Infos(Command command) {
        super(command);
         n = new NodeBuilder(
                create(value,
                        create("infos",create(new BundlesNode(true,true,false)))));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(!when(sArgs -> {
            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(args[1]);
            if(jvmExecutor == null){
                System.out.println("Cannot find executor");
                return false;
            }
            int p = jvmExecutor.getConfig().getPort();
            String port = p == 0 ? "auto" : String.valueOf(p);
            System.out.println(Colors.GREEN+"Name: "+Colors.RESET+jvmExecutor.getName());
            System.out.println(Colors.GREEN+"Bundle: "+Colors.RESET+jvmExecutor.getBundleData().getName());
            System.out.println(Colors.GREEN+"Type: "+Colors.RESET+(jvmExecutor.isProxy() ? "Proxy" : "Server"));
            System.out.println(Colors.GREEN+"Port: "+Colors.RESET+port);
            System.out.println(Colors.GREEN+"Min Ram: "+Colors.RESET+jvmExecutor.getConfig().getXms());
            System.out.println(Colors.GREEN+"Max Ram: "+Colors.RESET+jvmExecutor.getConfig().getXmx());
            System.out.println(Colors.GREEN+"Java Version: "+Colors.RESET+jvmExecutor.getConfig().getJavaVersion());
            System.out.println(Colors.GREEN+"Default-Mode: "+Colors.RESET+jvmExecutor.getConfig().getType());
            if(!jvmExecutor.getConfig().getDeployers().isEmpty()){
                System.out.println(Colors.GREEN+"Deployers: "+Colors.RESET+jvmExecutor.getConfig().getDeployers());
            }
            return true;
        },args,"infos","data")){

            return true;
        }
        return true;
    }
}
