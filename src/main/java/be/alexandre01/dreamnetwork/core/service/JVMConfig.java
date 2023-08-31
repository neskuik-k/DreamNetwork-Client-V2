package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter
public class JVMConfig extends ConfigData implements IConfig {
    @Ignore protected String name;
    @Ignore String pathName;

    YamlFileUtils<JVMConfig> ymlFile;
    public JVMConfig(){
        // Empty constructor
        ymlFile = new YamlFileUtils<>();
    }

    public void config(File file){
        ymlFile.representer = new CustomRepresenter(true,JVMConfig.class);
        ((CustomRepresenter)ymlFile.representer).setThisClassOnly(true);
        ymlFile.config(file,JVMConfig.class,true);
    }
    public JVMConfig(File file){
        ymlFile.config(file,JVMConfig.class,true);
        // Empty constructor
    }

    public JVMConfig(File file, String name, JVMExecutor.Mods type, String xms, String startup, String executable, String xmx, String pathName, String javaVersion, int port){
        ymlFile = new YamlFileUtils<>();
        ymlFile.config(file,JVMConfig.class,true);
        this.name = name;
        this.type = type;
        this.xms = xms;
        this.startup = startup;
        this.executable = executable;
        this.xmx = xmx;
        this.pathName = pathName;
        this.javaVersion = javaVersion;
        this.port = port;
    }
}
