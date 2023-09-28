package be.alexandre01.dreamnetwork.core.service.tasks;


import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.service.tasks.IGlobalTasks;
import be.alexandre01.dreamnetwork.api.service.tasks.ATaskData;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Synchronized;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class GlobalTasks extends YamlFileUtils<GlobalTasks> implements IGlobalTasks {

    @Getter public final List<ATaskData> tasks = new ArrayList<>();
    @Getter private final List<ATaskData> alwaysONs = new ArrayList<>();
    @Ignore
    private HashMap<String,ATaskData> withNames = new HashMap<>();

    ScheduledExecutorService executorService = null;

    public GlobalTasks() {
        addTag(GlobalTasks.class,Tag.MAP);
        addTag(ATaskData.class,Tag.MAP);
        representer = new CustomRepresenter(true,GlobalTasks.class,ATaskData.class);
        representer.addClassTag(ATaskData.class, Tag.MAP);
        representer.addClassTag(TaskData.class, Tag.MAP);
        super.dumperOptions.setExplicitEnd(true);
        constructor = new Constructor(GlobalTasks.class,new LoaderOptions());
        TypeDescription taskDescription = new TypeDescription(GlobalTasks.class);
        taskDescription.putListPropertyType("tasks", ATaskData.class);
        taskDescription.putListPropertyType("tasks", TaskData.class);

    }

    public void loading(){
        addAnnotation("Configure your tasks here");
        alwaysONs.clear();
        withNames.clear();
        tasks.clear();

        if(!super.config(new File(Config.getPath("data/Tasks.yml")), GlobalTasks.class,true)){
            super.saveFile(GlobalTasks.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }

        for (ATaskData taskData : tasks) {
            withNames.put(taskData.getName(),taskData);
           loadTask(taskData);
        }

        // Init

        enable();
    }

    @Override
    public ATaskData getTask(String name){
        return withNames.get(name);
    }

    @Override
    public void addTask(ATaskData taskData){
        tasks.add(taskData);
        withNames.put(taskData.getName(),taskData);
        loadTask(taskData);
    }

    private void loadTask(ATaskData taskData){
        if(taskData.getTaskType().equals(ATaskData.TaskType.ALWAYS_ON)){
            alwaysONs.add(taskData);
        }
        if(!(taskData.getTaskType().equals(ATaskData.TaskType.MANUAL) || taskData.getTaskType().equals(ATaskData.TaskType.MANUAL_RESTRICTED)))
            taskData.operate();
    }


    @Override
    public void save(){
        super.saveFile(GlobalTasks.class.cast(this));
    }


    @Override
    public void enable() {
        if(executorService != null)return;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            for(ATaskData taskData : alwaysONs){
                taskData.operate();
            }
        }, 3,3, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("Tasks enabled");
    }

    @Override
    public void disable() {
        if(executorService == null)return;
        executorService.shutdown();
        executorService = null;
        System.out.println("Tasks disabled");
    }
}
