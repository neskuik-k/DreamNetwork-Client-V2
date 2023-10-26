package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.events.EventCatcher;
import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Setter @Getter @JsonIgnoreProperties @JsonIgnoreType
public class JVMConfig extends ConfigData implements IConfig {
    @JsonIgnore
    @Ignore String pathName;

    @Ignore @JsonIgnore YamlFileUtils<ConfigData> ymlFile;
    public JVMConfig(){
        // Empty constructor
        ymlFile = new YamlFileUtils<>(ConfigData.class);
    }

    public void config(File file){
        ymlFile.init(file,true,false);
        //ymlFile.representer = new CustomRepresenter(true,ConfigData.class);
      //  ((CustomRepresenter)ymlFile.representer).setThisClassOnly(true);
       // ymlFile.config(file,ConfigData.class,true);
    }
    public JVMConfig(File file){
       // ymlFile.config(file,ConfigData.class,true);
        // Empty constructor
    }

    public JVMConfig(File file, String name, JVMExecutor.Mods type, String xms, String startup, String executable, String xmx, String pathName, String javaVersion, int port,String bundleName){
        ymlFile = new YamlFileUtils<>(ConfigData.class);
        ymlFile.config(file,ConfigData.class,true);
        this.name = name;
        this.type = type;
        this.xms = xms;
        this.startup = startup;
        this.executable = executable;
        this.xmx = xmx;
        this.pathName = pathName;
        this.javaVersion = javaVersion;
        this.port = port;
        this.bundleName = bundleName;
    }

}
