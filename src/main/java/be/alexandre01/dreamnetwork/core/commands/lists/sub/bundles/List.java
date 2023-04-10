package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class List extends SubCommand {
    public List(){
        NodeBuilder nodeBuilder = new NodeBuilder(
                create("bundle",
                    create("list")));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        boolean b = when(sArgs -> {
            for (BundleData bundleData : Main.getInstance().getBundleManager().getBundleDatas().values()){
                System.out.println(LanguageManager.getMessage("commands.bundle.list.bundleName").replaceFirst("%var%", bundleData.getBundleInfo().getName()));
                System.out.println(LanguageManager.getMessage("commands.bundle.list.bundleType").replaceFirst("%var%", String.valueOf(bundleData.getBundleInfo().getType())));
                System.out.println(LanguageManager.getMessage("commands.bundle.list.bundleExecutors").replaceFirst("%var%", String.valueOf(bundleData.getExecutors())));
            }
            return true;
        },args,"list");
        return b;
    }

}
