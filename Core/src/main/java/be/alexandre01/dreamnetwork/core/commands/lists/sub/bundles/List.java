package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Main;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class List extends SubCommand {
    public List(Command command){
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                    create("list")));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        boolean b = when(sArgs -> {
            for (BundleData bundleData : Main.getInstance().getBundleManager().getBundleDatas().values()){
                Console.printLang("commands.bundle.list.bundleName", bundleData.getBundleInfo().getName());
                Console.printLang("commands.bundle.list.bundleType", bundleData.getBundleInfo().getType());
                Console.printLang("commands.bundle.list.bundleExecutors", bundleData.getExecutors());
            }
            return true;
        },args,"list");
        return b;
    }

}
