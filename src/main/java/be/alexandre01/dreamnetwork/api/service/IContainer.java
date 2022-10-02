package be.alexandre01.dreamnetwork.api.service;

import java.util.HashMap;

public interface IContainer {
    IJVMExecutor getJVMExecutor(String processName, JVMType jvmType);

    public enum JVMType {
        SERVER, PROXY
    }

    public IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile);

    public void stop(String name,String pathName);

    public HashMap<String, IJVMExecutor> getJVMExecutorsServers();

    public HashMap<String, IJVMExecutor> getJVMExecutorsProxy();
}
