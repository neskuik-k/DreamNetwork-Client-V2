package be.alexandre01.dreamnetwork.core.commands.lists.sub.deploys;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.core.commands.lists.DeployCommand;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.service.deployment.DeployData;
import lombok.NonNull;

import java.io.File;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Create extends SubCommand {
    public Create(DeployCommand command) {
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("create")));
    }


    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        System.out.println("Create");
        if(!when(new SubCommandExecutor() {
            @Override
            public boolean onSubCommand(@NonNull String[] args) {
                String folder = args[1];
                DeployData.DeployType type;

                if(Config.contains("deploys/"+folder)){
                    System.out.println("This folder already exists");
                    return false;
                }


                Config.createDir("deploys/"+folder);

                DeployData deployData = new DeployData();
                deployData.loading(new File("deploys/"+folder+"/deploy.yml"));
                return true;
            }
        },args,"create","folder")) {
            System.out.println("Not true");
            fail("deploy","create","folder");
            return true;
        }
        return true;
    }
}
