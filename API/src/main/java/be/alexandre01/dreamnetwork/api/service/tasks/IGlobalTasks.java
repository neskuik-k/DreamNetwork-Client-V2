package be.alexandre01.dreamnetwork.api.service.tasks;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public interface IGlobalTasks {
    ITaskData getTask(String name);

    void addTask(ITaskData taskData);

    void save();

    void enable();

    void disable();

    java.util.List<ITaskData> getTasks();

    java.util.List<ITaskData> getAlwaysONs();
}
