package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import lombok.NonNull;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class List extends SubCommandCompletor implements SubCommandExecutor {
    public List(){
        setCompletion(node("service",
                node("list")));
        addCompletor("service","screen");
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("list")){
            System.out.println("Services for proxy:");
            for(IJVMExecutor executor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                System.out.println(executor.getName());
            }
            System.out.println("Services for spigot:");
            for(IJVMExecutor executor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                System.out.println(executor.getName());
            }
            return true;
        }
        return false;
    }

}
