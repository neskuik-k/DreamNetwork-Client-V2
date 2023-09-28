package be.alexandre01.dreamnetwork.api.service.tasks;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public interface IGlobalTasks {
    ATaskData getTask(String name);

    void addTask(ATaskData taskData);

    void save();

    void enable();

    void disable();

    java.util.List<ATaskData> getTasks();

    java.util.List<ATaskData> getAlwaysONs();
}
