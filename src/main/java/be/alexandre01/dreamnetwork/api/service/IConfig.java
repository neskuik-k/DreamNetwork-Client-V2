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

    public void setName(String name);

    public void setType(JVMExecutor.Mods type);

    public void setXms(String xms);

    public void setStartup(String startup);

    public void setExec(String exec);

    public void setXmx(String xmx);

    public void setPathName(String pathName);

    public void setJavaVersion(String javaVersion);

    public void setPort(int port);

}
