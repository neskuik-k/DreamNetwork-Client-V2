package be.alexandre01.dreamnetwork.core.service.tasks;

import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.service.tasks.TaskData;
import be.alexandre01.dreamnetwork.core.Core;

import java.util.Optional;

public class TaskOperation {

    public Runnable createOperation(TaskData taskData) {
        if (taskData.getJvmExecutor() == null) {
            Optional<IExecutor> jvmExecutor = Core.getInstance().getJvmContainer().findExecutor(taskData.getService());
            if (!jvmExecutor.isPresent()) {
                System.out.println("Service " + taskData.getService() + " not found");
                return null;
            }
            taskData.setJvmExecutor(jvmExecutor.get());
        }

        IExecutor jvmExecutor = taskData.getJvmExecutor();
        if (taskData.getIConfig() == null) {
            String profile = taskData.getProfile();
            if (profile != null && taskData.getJvmExecutor().getJvmProfiles().isPresent()) {
                IProfiles iProfiles = taskData.getJvmExecutor().getJvmProfiles().get();
                if (iProfiles.getProfiles().containsKey(profile)) {
                    taskData.setIConfig(iProfiles.getProfiles().get(profile));
                }
                if (!(jvmExecutor instanceof IConfig)) {
                    Core.getInstance().getGlobalTasks().getTasks().remove(taskData);
                }
                taskData.setIConfig(IStartupConfig.builder(taskData.getIConfig()).buildFrom((IStartupConfig) jvmExecutor));
            } else {
                if (jvmExecutor instanceof IConfig) {
                    taskData.setIConfig((IConfig) jvmExecutor);
                } else {
                    Core.getInstance().getGlobalTasks().getTasks().remove(taskData);
                }
            }
        }

        return () -> {
            int toStart = 0;
            int count = taskData.getCount();
            int actualCount = taskData.getActualCount();
            TaskData.TaskType taskType = taskData.getTaskType();
            if (!taskType.equals(TaskData.TaskType.MANUAL)) {
                if ((count - actualCount) <= 0)
                    return;
                toStart = count - actualCount;
            } else {
                toStart = count;
            }

            if(taskData.getProfile() != null){
                System.out.println("Starting " + (count - actualCount) + " " + taskData.getService() + " with profile " + taskData.getProfile());
            }else {
                System.out.println("Starting " + (count - actualCount) + " " + taskData.getService());
            }

            //actualCount += toStart;
            //toStart = count - actualCount;
            taskData.setActualCount(actualCount+toStart);

           // System.out.println("ActualCount = "+ taskData.getActualCount());


            jvmExecutor.startServers(toStart, taskData.getIConfig()).whenFail(new ExecutorCallbacks.ICallbackFail() {
                @Override
                public void whenFail() {
                   // System.out.println("Fail Decrease count to "+(taskData.getActualCount()-1));
                    taskData.decreaseCount();
                }
            }).whenStop(new ExecutorCallbacks.ICallbackStop() {
                @Override
                public void whenStop(IService service) {
                    //System.out.println("Stop Decrease count to "+(taskData.getActualCount()-1));
                    taskData.decreaseCount();
                }
            });
        };
    }

}

