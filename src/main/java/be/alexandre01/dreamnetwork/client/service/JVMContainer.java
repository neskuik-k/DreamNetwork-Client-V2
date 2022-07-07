package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;

import java.util.HashMap;

public class JVMContainer implements be.alexandre01.dreamnetwork.api.service.IContainer {
    public volatile HashMap<String, JVMExecutor> jvmExecutorsServers = new HashMap<>();
    public volatile HashMap<String, JVMExecutor> jvmExecutorsProxy = new HashMap<>();


    @Override
    public synchronized JVMExecutor getJVMExecutor(String processName, JVMType jvmType){
        switch (jvmType){
            case SERVER:
                return jvmExecutorsServers.get(processName);
            case PROXY:
                return jvmExecutorsProxy.get(processName);
        }
        return null;
    }

    public synchronized void addExecutor(JVMExecutor jvmExecutor, JVMType jvmType){
        switch (jvmType){
            case SERVER:
                jvmExecutorsServers.put(jvmExecutor.getName(),jvmExecutor);
                break;
            case PROXY:
                jvmExecutorsProxy.put(jvmExecutor.getName(),jvmExecutor);
                break;
        }

    }

}
