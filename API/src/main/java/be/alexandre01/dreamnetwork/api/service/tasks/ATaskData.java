package be.alexandre01.dreamnetwork.api.service.tasks;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import lombok.Getter;
import lombok.Setter;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/

@Getter @Setter
public abstract class ATaskData {
    public String name;
    public String service;
    public int count;
    public ATaskData.TaskType taskType;
    public String profile = null;

    @Ignore
    protected int actualCount = 0;
    @Ignore
    protected IJVMExecutor jvmExecutor;
    @Ignore
    protected IConfig iConfig;
    public static enum TaskType {
        ALWAYS_ON, ON_START, MANUAL, MANUAL_RESTRICTED
    }
    public abstract void operate();
}
