package be.alexandre01.dreamnetwork.api.service.tasks;

import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public abstract class AGlobalTasks extends YamlFileUtils<AGlobalTasks> {
    @Getter
    public final List<ATaskData> tasks = new ArrayList<>();
    @Getter @Ignore
    public final List<ATaskData> alwaysONs = new ArrayList<>();

    protected abstract ATaskData getTask(String name);

    public abstract void addTask(ATaskData taskData);

    public abstract void save();

    public abstract void enable();

    public abstract void disable();

    public abstract java.util.List<ATaskData> getTasks();

    public abstract java.util.List<ATaskData> getAlwaysONs();
}
