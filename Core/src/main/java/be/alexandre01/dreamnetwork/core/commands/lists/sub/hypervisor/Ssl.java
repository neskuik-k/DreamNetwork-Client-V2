package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.gui.ssl.SslMenu;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Ssl extends SubCommand {
    public Ssl(Command command) {
        super(command);
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("connect")));
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {


        boolean b = when(sArgs -> {
            SslMenu menu = new SslMenu("m:ssl");
            menu.buildAndRun();
            menu.show();
            return true;
        }, args,"ssl");
        return b;
    }

    public void reloadNode(){
        IConsoleReader.reloadCompleters();
    }
}
