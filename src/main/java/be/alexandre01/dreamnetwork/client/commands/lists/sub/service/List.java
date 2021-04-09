package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import lombok.NonNull;

public class List implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("list")){
            System.out.println("Services for proxy:");
            for(JVMExecutor executor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                System.out.println(executor.getName());
            }
            System.out.println("Services for spigot:");
            for(JVMExecutor executor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                System.out.println(executor.getName());
            }
            return true;
        }
        return false;
    }

}
