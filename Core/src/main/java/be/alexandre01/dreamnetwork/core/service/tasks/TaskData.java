package be.alexandre01.dreamnetwork.core.service.tasks;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.api.service.tasks.ATaskData;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;

import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class TaskData extends ATaskData {




    @Override
    public void operate() {

        if (jvmExecutor == null) {
            Optional<IJVMExecutor> jvmExecutor = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(service);
            if (!jvmExecutor.isPresent()) {
                System.out.println("Service " + service + " not found");
                return;
            }
            this.jvmExecutor = jvmExecutor.get();
        }
        if (iConfig == null) {
            if (profile != null) {
                if (jvmExecutor.getJvmProfiles().getProfiles().containsKey(profile)) {
                    iConfig = jvmExecutor.getJvmProfiles().getProfiles().get(profile);
                }
                if (!(jvmExecutor instanceof IConfig)) {
                    Core.getInstance().getGlobalTasks().tasks.remove(this);
                }
                iConfig = IStartupConfig.builder(iConfig).buildFrom((IStartupConfig) jvmExecutor);
            } else {
                if (jvmExecutor instanceof IConfig) {
                    iConfig = (IConfig) jvmExecutor;
                } else {
                    Core.getInstance().getGlobalTasks().tasks.remove(this);
                }
            }
        }
        int toStart = 0;
        if (!taskType.equals(TaskType.MANUAL)) {
            if ((count - actualCount) > 0)
                toStart = count - actualCount;
        } else {
            toStart = count;
        }
        if(toStart == 0){
            return;
        }
        System.out.println("Starting " + (count - actualCount) + " " + service + " with profile " + profile);
        actualCount += toStart;
        jvmExecutor.startServers(toStart, iConfig).whenFail(new ExecutorCallbacks.ICallbackFail() {
            @Override
            public void whenFail() {
                actualCount--;
            }
        }).whenStop(new ExecutorCallbacks.ICallbackStop() {
            @Override
            public void whenStop(IService service) {
                actualCount--;
            }
        });
    }

}

