package be.alexandre01.dreamnetwork.client.service;

import java.util.HashMap;

public class JVMContainer {
    public volatile HashMap<String, JVMExecutor> jvmExecutorsServers = new HashMap<>();
    public volatile HashMap<String, JVMExecutor> jvmExecutorsProxy = new HashMap<>();


    public synchronized JVMExecutor getJVMExecutor(String processName,JVMType jvmType){
        switch (jvmType){
            case SERVER:
                return jvmExecutorsServers.get(processName);
            case PROXY:
                return jvmExecutorsProxy.get(processName);
        }
        return null;
    }
    public synchronized void addExecutor(JVMExecutor jvmExecutor,JVMType jvmType){
        switch (jvmType){
            case SERVER:
                jvmExecutorsServers.put(jvmExecutor.name,jvmExecutor);
                break;
            case PROXY:
                jvmExecutorsProxy.put(jvmExecutor.name,jvmExecutor);
                break;
        }

    }

    public enum JVMType{
        SERVER,PROXY
    }
}
