package be.alexandre01.dreamnetwork.core.commands.lists.sub.tasks;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.service.tasks.TaskData;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.gui.tasks.GlobalTaskCreateConsole;
import be.alexandre01.dreamnetwork.core.service.deployment.DeployData;
import lombok.NonNull;
import org.jline.builtins.Completers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Global extends SubCommand {
    public Global(Command command) {
        super(command);
        List<TaskData> value = Core.getInstance().getGlobalTasks().getTasks();
        Object[] v = value.stream().map(TaskData::getName).toArray();

        if(v.length == 0){
            v = new Object[]{Completers.AnyCompleter.INSTANCE};
        }
       // System.out.println(v.size());


        NodeBuilder nodeBuilder = new NodeBuilder(
                create(this.value,
                        create("global", create("create","enable","disable"),
                                create("start",create(v)),
                                create("add", create(Completers.AnyCompleter.INSTANCE,create(v))),
                                create("remove", create(Completers.AnyCompleter.INSTANCE,create(v))),
                                create("list"),
                                create("set", create(Completers.AnyCompleter.INSTANCE,create(v))))));
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
                if(args[1].equalsIgnoreCase("enable")){
                    Core.getInstance().getGlobalTasks().enable();
                }
                if(args[1].equalsIgnoreCase("disable")){
                    Core.getInstance().getGlobalTasks().disable();
                }

                if(args[1].equalsIgnoreCase("list")){
                    Core.getInstance().getGlobalTasks().getTasks().forEach(taskData -> {
                        System.out.println(taskData.getName() + " " + taskData.count);
                    });
                }
                if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        System.out.println("Please specify how many tasks you want to add and your task");
                        System.out.println("Example: global add 2 taskName");
                        return true;
                    }
                    int i = 0;

                    try{
                        i = Integer.parseInt(args[2]);
                    }catch (NumberFormatException e){
                        System.out.println("Please specify a number");
                        return true;
                    }

                    if(args.length < 4){
                        System.out.println("Please specify a task name");
                        return true;
                    }

                    TaskData t = Core.getInstance().getGlobalTasks().getTask(args[3]);
                    if(t == null){
                        System.out.println("Task not found");
                        return true;
                    }

                    if(args[1].equalsIgnoreCase("add")){
                        t.count += i;
                        System.out.println(i + " task count added to " + t.getName());
                    }

                    if(args[1].equalsIgnoreCase("remove")){
                        t.count -= i;
                        System.out.println(i + " task count removed to " + t.getName());
                    }

                    if(args[1].equalsIgnoreCase("set")){
                        t.count = i;
                        System.out.println("Task count set to " + i + " for " + t.getName());
                    }
                }


                if(args[1].equalsIgnoreCase("start")){
                    if(args.length < 3){
                        System.out.println("Please specify a task name");
                        return true;
                    }

                    TaskData t = Core.getInstance().getGlobalTasks().getTask(args[2]);
                    if(t == null){
                        System.out.println("Task not found");
                        return true;
                    }
                    t.operate();

                    System.out.println("Task started");


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
