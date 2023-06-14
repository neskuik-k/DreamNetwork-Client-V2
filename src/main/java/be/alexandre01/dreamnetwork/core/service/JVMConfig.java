package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter
public class JVMConfig extends YamlFileUtils<JVMConfig> implements IConfig {
    @Ignore String name;
    JVMExecutor.Mods type;
    String xms;
    @Getter String startup = null;
    String executable = "exec";
    @Getter String xmx;
    @Ignore @Getter String pathName;
    @Getter String javaVersion = "default";
    @Getter int port = 0;
    String installInfo = null;
    Boolean screenEnabled = null;
    @Getter
    List<String> deployers = new ArrayList<>();


    public JVMConfig(){

    }

    public void config(File file){
        representer = new CustomRepresenter(true,JVMConfig.class);
        ((CustomRepresenter)representer).setThisClassOnly(true);
        config(file,JVMConfig.class,true);
    }
    public JVMConfig(File file){
        config(file,JVMConfig.class,true);
        // Empty constructor
    }

    public JVMConfig(File file, String name, JVMExecutor.Mods type, String xms, String startup, String executable, String xmx, String pathName, String javaVersion, int port){
        config(file,JVMConfig.class,true);
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
