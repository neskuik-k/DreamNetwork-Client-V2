package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.client.service.JVMExecutor;

public interface IConfig {
    public String getName();
    public JVMExecutor.Mods getType();
    public String getXms();
    public String getStartup();

    public String getExec();
    public String getXmx();
    public String getPathName();
    public String getJavaVersion();
    public int getPort();
}
