package be.alexandre01.dreamnetwork.core.service.tasks;


import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.service.tasks.GlobalTasksData;
import be.alexandre01.dreamnetwork.api.service.tasks.IGlobalTasks;
import be.alexandre01.dreamnetwork.api.service.tasks.TaskData;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.Main;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class GlobalTasks extends YamlFileUtils<GlobalTasksData> implements IGlobalTasks {
    GlobalTasksData data;
    @Ignore
    private HashMap<String, TaskData> withNames = new HashMap<>();

    ScheduledExecutorService executorService = null;

    public GlobalTasks() {
        super(GlobalTasksData.class);
        addTag(GlobalTasksData.class,Tag.MAP);
        addTag(TaskData.class,Tag.MAP);
        representer = new CustomRepresenter(true,GlobalTasksData.class, TaskData.class);
        representer.addClassTag(TaskData.class, Tag.MAP);
        representer.addClassTag(TaskOperation.class, Tag.MAP);
        super.dumperOptions.setExplicitEnd(true);
        constructor = new Constructor(GlobalTasksData.class,new LoaderOptions());
        TypeDescription taskDescription = new TypeDescription(GlobalTasksData.class);
        taskDescription.putListPropertyType("tasks", TaskData.class);
        taskDescription.putListPropertyType("tasks", TaskOperation.class);

    }

    public void loading(){
       // data = new GlobalTasksData();
        addAnnotation("Configure your tasks here");
        /*data.alwaysONs.clear();
        withNames.clear();
        data.tasks.clear();*/

        init(new File(Config.getPath("data/Tasks.yml")),true).ifPresent(globalTasksData -> {
            data = globalTasksData;
        });
    }

    public void loadTasks(){
        for (TaskData taskData : data.tasks) {
            withNames.put(taskData.getName(),taskData);
            taskData.setOperation(Main.getTaskOperation().createOperation(taskData));
            loadTask(taskData);
        }

        // Init
        enable();
    }

    @Override
    public TaskData getTask(String name){
        return withNames.get(name);
    }

    @Override
    public void addTask(TaskData taskData){
        data.tasks.add(taskData);
        withNames.put(taskData.getName(),taskData);
        loadTask(taskData);
    }

    private void loadTask(TaskData taskData){
        if(taskData.getTaskType().equals(TaskData.TaskType.ALWAYS_ON)){
            data.alwaysONs.add(taskData);
        }
        if(!(taskData.getTaskType().equals(TaskData.TaskType.MANUAL) || taskData.getTaskType().equals(TaskData.TaskType.MANUAL_RESTRICTED)))
            taskData.operate();
    }


    @Override
    public void save(){
        super.saveFile((GlobalTasksData) data);
    }


    @Override
    public void enable() {
        if(executorService != null)return;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            for(TaskData taskData : data.alwaysONs){
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

    @Override
    public List<TaskData> getTasks() {
        return data.getTasks();
    }

    @Override
    public List<TaskData> getAlwaysONs() {
        return data.getAlwaysONs();
    }
}
