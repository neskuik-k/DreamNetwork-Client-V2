package be.alexandre01.dreamnetwork.api.service.tasks;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public interface IGlobalTasks {
    TaskData getTask(String name);

    void addTask(TaskData taskData);

    void save();

    void enable();

    void disable();

    void loadTasks();

    java.util.List<TaskData> getTasks();

    java.util.List<TaskData> getAlwaysONs();
}
