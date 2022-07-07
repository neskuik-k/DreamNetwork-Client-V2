package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.client.service.JVMExecutor;

public interface IContainer {
    IJVMExecutor getJVMExecutor(String processName, JVMType jvmType);

    public enum JVMType {
        SERVER, PROXY
    }
}
