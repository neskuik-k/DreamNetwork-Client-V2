package be.alexandre01.dreamnetwork.api.service.tasks;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/

@Getter @Setter
public class TaskData {
    public String name;
    public String service;
    @Ignore private transient Runnable operation;
    public int count;
    public TaskData.TaskType taskType;
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

    public void operate(){
        if(operation != null){
            operation.run();
        }
    }

    public void decreaseCount(){
        actualCount--;
    }

    public void increaseCount(){
        actualCount++;
    }
}
