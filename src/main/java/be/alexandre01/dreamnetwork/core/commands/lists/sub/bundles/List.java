package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.bundle.BService;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import be.alexandre01.dreamnetwork.core.utils.clients.TypeArgumentChecker;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
                System.out.println(Colors.GREEN+"Bundle name: "+bundleData.getBundleInfo().getName());
                System.out.println(Colors.GREEN+"Bundle type: "+bundleData.getBundleInfo().getType());
                System.out.println(Colors.GREEN+"Bundle executors: "+bundleData.getExecutors());
            }
            return true;
        },args,"list");
        return b;
    }

}
