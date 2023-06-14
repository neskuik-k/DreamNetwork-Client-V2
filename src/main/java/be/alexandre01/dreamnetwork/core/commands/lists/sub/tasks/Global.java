package be.alexandre01.dreamnetwork.core.commands.lists.sub.tasks;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.gui.tasks.GlobalTaskCreateConsole;
import be.alexandre01.dreamnetwork.core.service.deployment.DeployData;
import lombok.NonNull;

import java.io.File;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Global extends SubCommand {
    public Global(Command command) {
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("global",create("create"))));
    }


    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(!when(new SubCommandExecutor() {
            @Override
            public boolean onSubCommand(@NonNull String[] args) {
                if(args.length < 2){
                    return false;
                }

                if(args[1].equalsIgnoreCase("create")){
                    GlobalTaskCreateConsole globalTaskCreateConsole = new GlobalTaskCreateConsole();
                    globalTaskCreateConsole.buildAndRun();
                    //globalTaskCreateConsole.setSafeRemove(true);
                    globalTaskCreateConsole.show();
                }
                return true;
            }
        },args,"global","arg")) {
            System.out.println("Not true");
            fail("task","global","arg");
            return true;
        }
        return true;
    }
}
