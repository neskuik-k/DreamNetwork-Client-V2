package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.
        JVMStartupConfig;

import java.io.File;

public interface IStartupConfig extends IConfig {

    static IStartupConfigBuilder builder(){
        return new JVMStartupConfig.Builder();
    }
    void saveFile();

    boolean changePort(String pathName, String finalname, int port, IContainer.JVMType jvmType, JVMExecutor.Mods mods);

    Integer getCurrentPort(String pathName, String finalname, IContainer.JVMType jvmType, JVMExecutor.Mods mods);

    String getLine(String finalname);

    void addConfigsFiles();

    void updateConfigFile(String pathName, String finalName, JVMExecutor.Mods type, String Xms, String Xmx, int port, boolean proxy, String exec, String startup, String javaVersion);

    long getConfigSize();

    boolean hasExecutable();

    void setConfig(boolean isConfig);

    void setConfSize(long confSize);

    void setProxy(boolean proxy);

    void setFixedData(boolean fixedData);

    File getFileRootDir();

    boolean isConfig();

    long getConfSize();

    boolean isProxy();

    boolean isFixedData();

}
