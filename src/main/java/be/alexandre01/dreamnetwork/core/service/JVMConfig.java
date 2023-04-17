package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter @Getter
public class JVMConfig extends YamlFileUtils<JVMConfig> implements IConfig {
    @Ignore String name;
    JVMExecutor.Mods type;
    String xms;
    @Getter String startup = null;
    String exec = "exec";
    @Getter String xmx;
    @Ignore @Getter String pathName;
    @Getter String javaVersion = "default";
    @Getter int port = 0;

    public JVMConfig(){

    }

    public void config(File file){
        config(file,JVMConfig.class,true);
    }
    public JVMConfig(File file){
        config(file,JVMConfig.class,true);
        // Empty constructor
    }

    public JVMConfig(File file,String name, JVMExecutor.Mods type, String xms, String startup, String exec, String xmx, String pathName, String javaVersion, int port){
        config(file,JVMConfig.class,true);
        this.name = name;
        this.type = type;
        this.xms = xms;
        this.startup = startup;
        this.exec = exec;
        this.xmx = xmx;
        this.pathName = pathName;
        this.javaVersion = javaVersion;
        this.port = port;
    }
}
