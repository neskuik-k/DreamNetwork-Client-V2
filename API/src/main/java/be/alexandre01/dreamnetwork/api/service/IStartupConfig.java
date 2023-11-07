package be.alexandre01.dreamnetwork.api.service;


import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;

public interface IStartupConfig extends IConfig {


    static IStartupConfigBuilder builder(){
        return new ConfigBuilder();
    }
    static IStartupConfigBuilder builder(IConfig config){
        return new ConfigBuilder(config);
    }
    void saveFile();

    boolean changePort(String pathName, String finalname, int port,int defaultPort, IContainer.JVMType jvmType, IJVMExecutor.Mods mods);

    Integer getCurrentPort(String pathName, String finalname, IContainer.JVMType jvmType, IJVMExecutor.Mods mods);

    String getLine(String finalname);

    void addConfigsFiles();

    void updateConfigFile();

    void updateConfigFile(String pathName, String finalName, IJVMExecutor.Mods type, String Xms, String Xmx, int port, boolean proxy, String exec, String javaVersion,String customName);

    @JsonIgnore long getConfigSize();

    boolean hasExecutable();

    void setConfig(boolean isConfig);

    void setConfSize(long confSize);

    void setProxy(boolean proxy);

    void setFixedData(boolean fixedData);

    @JsonIgnore File getFileRootDir();

    @JsonIgnore boolean isConfig();

    @JsonIgnore long getConfSize();

    @JsonIgnore boolean isProxy();

    @JsonIgnore boolean isFixedData();

}
