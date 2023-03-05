package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;
import static org.jline.builtins.Completers.TreeCompleter.node;

public class List extends SubCommandCompletor implements SubCommandExecutor {
    public List(){
        /*setCompletion(node("service",
                node("list")));*/
        NodeBuilder nodeBuilder = new NodeBuilder(create("list"));
      //  addCompletor("service","screen");
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("list")){
            System.out.println("Services for proxy:");
            for(IJVMExecutor executor : Core.getInstance().getJvmContainer().getProxiesExecutors()){
                System.out.println(executor.getName());
            }
            System.out.println("Services for spigot:");
            for(IJVMExecutor executor : Core.getInstance().getJvmContainer().getServersExecutors()){
                System.out.println(executor.getName());
            }
            return true;
        }
        return false;
    }

}
