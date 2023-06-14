package be.alexandre01.dreamnetwork.core.service.tasks;

import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.service.JVMConfig;
import be.alexandre01.dreamnetwork.core.service.JVMProfiles;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class GlobalTasks extends YamlFileUtils<GlobalTasks> {

    @Getter public final List<TaskData> tasks = new ArrayList<>();
    @Getter private final List<TaskData> alwaysONs = new ArrayList<>();
    @Ignore private HashMap<String,TaskData> withNames = new HashMap<>();

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public GlobalTasks() {
        addTag(GlobalTasks.class,Tag.MAP);
        addTag(TaskData.class,Tag.MAP);
        representer = new CustomRepresenter(true,GlobalTasks.class,TaskData.class);
        representer.addClassTag(TaskData.class, Tag.MAP);

        constructor = new Constructor(GlobalTasks.class);
        TypeDescription taskDescription = new TypeDescription(GlobalTasks.class);
        taskDescription.putListPropertyType("tasks", TaskData.class);

    }

    public void loading(){
        addAnnotation("Configure your tasks here");
        if(!super.config(new File(Config.getPath("data/Tasks.yml")), GlobalTasks.class,true)){
            super.saveFile(GlobalTasks.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }

        for (TaskData taskData : tasks) {
            System.out.println(taskData.getName());
            withNames.put(taskData.getName(),taskData);
            if(taskData.getTaskType().equals(TaskData.TaskType.ALWAYS_ON)){
                alwaysONs.add(taskData);
            }
            taskData.operate();
        }

        // Init

        executorService.scheduleAtFixedRate(() -> {
            for(TaskData taskData : alwaysONs){
                taskData.operate();
            }
        }, 3,3, java.util.concurrent.TimeUnit.SECONDS);
    }

    public TaskData getTask(String name){
        return withNames.get(name);
    }

    public void addTask(TaskData taskData){
        tasks.add(taskData);
        withNames.put(taskData.getName(),taskData);
    }


    public void save(){
        super.saveFile(GlobalTasks.class.cast(this));
    }


}
