package be.alexandre01.dreamnetwork.core.gui.tasks;

import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.tasks.TaskData;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class GlobalTaskCreateConsole extends AccessibilityMenu {
    TaskData taskData = new TaskData();
    public GlobalTaskCreateConsole(){
        super("m:tasks");
            addValueInput(PromptText.create("taskName"), new ValueInput() {
                @Override
                public void onTransition(ShowInfos infos) {
                    infos.onEnter(Console.getFromLang("service.task.create.taskName"));
                }

                @Override
                public Operation received(PromptText value, String[] args, ShowInfos infos) {
                    if(value.getValue().replace(" ","").isEmpty()){
                        infos.error(Console.getFromLang("service.task.create.incorrectTaskName"));
                        return errorAndRetry(infos);
                    }
                    if(Core.getInstance().getGlobalTasks().getTask(args[0]) != null){
                        infos.error(Console.getFromLang("service.task.create.taskAlreadyExist"));
                        return errorAndRetry(infos);
                    }

                    taskData.setName(args[0]);
                    return Operation.accepted(args[0]);
                }
            });

        String[] types = new String[]{"ALWAYS_ON","ON_START","CUSTOM"};

        addValueInput(PromptText.create("type").setSuggestions(create((Object[]) types)), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter(Console.getFromLang("service.task.create.type"));
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(args.length == 0){
                    infos.error(Console.getFromLang("service.task.create.incorrectType"));
                    return errorAndRetry(infos);
                }
                try {
                    taskData.setTaskType(TaskData.TaskType.valueOf(args[0]));
                }catch (Exception e){
                    infos.error(Console.getFromLang("service.task.create.incorrectType"));
                    return errorAndRetry(infos);
                }
                return skip();
            }
        });

        addValueInput(PromptText.create("count"), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter(Console.getFromLang("service.task.create.count"));
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(args.length == 0){
                    infos.error(Console.getFromLang("service.task.create.incorrectCount"));
                    return errorAndRetry(infos);
                }
                try {
                    taskData.setCount(Integer.parseInt(args[0]));
                }catch (Exception e){
                    infos.error(Console.getFromLang("service.task.create.incorrectCount"));
                    return errorAndRetry(infos);
                }
                return skip();
            }
        });

        addValueInput(PromptText.create("services").setSuggestions(create(new BundlesNode(true,true,false),create("with"))), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter(Console.getFromLang("service.task.create.service"));
                int number = taskData.count;
                infos.writing(console.writing+" "+ Colors.YELLOW+number+" of "+Colors.RESET);
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                System.out.println(args.length);
                if(args.length == 0){
                    infos.error(Console.getFromLang("service.task.create.incorrectService"));
                    return errorAndRetry(infos);
                }
                taskData.setService(args[0]);
                if(args.length > 1){
                    if(args[1].equalsIgnoreCase("with")){
                        if(args.length > 2){
                            taskData.setProfile(args[2]);
                        }else {
                            infos.error(Console.getFromLang("service.task.create.incorrectService"));
                            return errorAndRetry(infos);
                        }
                    }
                }

                Core.getInstance().getGlobalTasks().addTask(taskData);
                Core.getInstance().getGlobalTasks().save();
                Console.print("Adding data to the task");

                forceExit();
                return finish();
            }
        });

    }
}
