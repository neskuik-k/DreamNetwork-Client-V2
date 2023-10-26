package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.utils.Tuple;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IConfig {
    public String getName();

    public IJVMExecutor.Mods getType();

    public String getXms();

    public String getStartup();

    public String getExecutable();

    public String getXmx();

    public String getPathName();

    public String getJavaVersion();

    public int getPort();

    public void setName(String name);

    public void setType(IJVMExecutor.Mods type);

    public void setXms(String xms);

    public void setStartup(String startup);

    public void setExecutable(String executable);

    public void setXmx(String xmx);

    public void setPathName(String pathName);

    public void setJavaVersion(String javaVersion);

    public void setPort(int port);

    public String getInstallInfo();
    public void setInstallInfo(String info);

    public Boolean getScreenEnabled();
    public void setScreenEnabled(Boolean screenEnabled);

    public List<String> getDeployers();
    public List<String> getStaticDeployers();
    public void setDeployers(List<String> deployers);
}
