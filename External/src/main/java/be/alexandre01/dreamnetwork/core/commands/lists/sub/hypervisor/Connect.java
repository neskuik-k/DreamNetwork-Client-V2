package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;

import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Connect extends SubCommand {
    public Connect(Command command) {
        super(command);
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("connect")));

    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {


        boolean b = when(sArgs -> {
            if(sArgs.length == 1){
                Console.printLang("commands.hypervisor.specifyModule");
                return true;
            }

            String ip = args[1];


            ExternalCore.getInstance().initialize(ip);

            return true;
        }, args,"connect","[ip]");
        return b;
    }

    public void reloadNode(){
        IConsoleReader.reloadCompleters();
    }
}
