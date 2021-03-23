package be.alexandre01.dreamnetwork.client.service;

import java.util.HashMap;

public class JVMContainer {
    public volatile HashMap<String, JVMExecutor> jvmExecutors = new HashMap<>();


    public synchronized JVMExecutor getJVMExecutor(String processName){
        return jvmExecutors.get(processName);
    }
    public synchronized void addExecutor(JVMExecutor jvmExecutor){
        jvmExecutors.put(jvmExecutor.name,jvmExecutor);
    }


}
