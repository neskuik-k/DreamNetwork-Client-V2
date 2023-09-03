package be.alexandre01.dreamnetwork.api.service.tasks;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public interface ITaskData {
    public static enum TaskType {
        ALWAYS_ON, ON_START, MANUAL, MANUAL_RESTRICTED
    }
    void operate();

    String getName();

    String getService();

    int getCount();

    ITaskData.TaskType getTaskType();

    String getProfile();

    int getActualCount();

    be.alexandre01.dreamnetwork.api.service.IJVMExecutor getJvmExecutor();

    be.alexandre01.dreamnetwork.api.service.IConfig getIConfig();

    void setName(String name);

    void setService(String service);

    void setCount(int count);

    void setTaskType(ITaskData.TaskType taskType);

    void setProfile(String profile);

    void setActualCount(int actualCount);

    void setJvmExecutor(be.alexandre01.dreamnetwork.api.service.IJVMExecutor jvmExecutor);

    void setIConfig(be.alexandre01.dreamnetwork.api.service.IConfig iConfig);
}
