package be.alexandre01.dreamnetwork.core.service.enums;

import be.alexandre01.dreamnetwork.api.service.IContainer;

public enum ExecType {
    SPIGOT(false),BUNGEECORD(true),VELOCITY(true);


    private IContainer.JVMType jvmType;

    ExecType(boolean isProxy) {
        jvmType = isProxy ? IContainer.JVMType.PROXY : IContainer.JVMType.SERVER;
    }

    public IContainer.JVMType getJvmType() {
        return jvmType;
    }

    public boolean isProxy() {
        return jvmType == IContainer.JVMType.PROXY;
    }
}
