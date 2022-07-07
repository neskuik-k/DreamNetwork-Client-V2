package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.client.service.JVMConfig;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMStartupConfig;

public interface IStartupConfig extends IConfig {

    static IStartupConfigBuilder builder(){
        return new JVMStartupConfig.Builder();
    }
    void update();

    boolean changePort(String pathName, String finalname, int port, JVMExecutor.Mods type);

    Integer getCurrentPort(String pathName, String finalname, JVMExecutor.Mods type);

    String getLine(String finalname);

    void addConfigsFiles();

    void updateConfigFile(String pathName, String finalName, JVMExecutor.Mods type, String Xms, String Xmx, int port, boolean proxy, String exec, String startup, String javaVersion);

    long getConfigSize();

    boolean hasExecutable();

    void setConfig(boolean isConfig);

    void setConfSize(long confSize);

    void setProxy(boolean proxy);

    void setFixedData(boolean fixedData);

    void setFileRootDir(java.io.File fileRootDir);

    boolean isConfig();

    long getConfSize();

    boolean isProxy();

    boolean isFixedData();

    java.io.File getFileRootDir();
}
