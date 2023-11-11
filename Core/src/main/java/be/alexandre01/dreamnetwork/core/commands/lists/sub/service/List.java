package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class List extends SubCommandCompletor implements SubCommandExecutor {
    public List(Command command){
        super(command);
        /*setCompletion(node("service",
                node("list")));*/
        NodeBuilder nodeBuilder = new NodeBuilder(create(value, create("list")));
      //  addCompletor("service","screen");
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("list")){
            Console.printLang("commands.service.list.proxy");
            for(IExecutor executor : Core.getInstance().getJvmContainer().getProxiesExecutors()){
                System.out.println(Colors.RED+executor.getBundleData().getName()+Colors.YELLOW+"/"+Colors.WHITE_BOLD_BRIGHT+executor.getName());
            }
            Console.printLang("commands.service.list.spigot");
            for(IExecutor executor : Core.getInstance().getJvmContainer().getServersExecutors()){
                System.out.println(Colors.CYAN+executor.getBundleData().getName()+Colors.YELLOW+"/"+Colors.WHITE_BOLD_BRIGHT+executor.getName());
            }
            return true;
        }
        return false;
    }

}
