package be.alexandre01.dreamnetwork.client.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Setter @Getter
public class JVMConfig {
    String name;
    JVMExecutor.Mods type;
    String xms;
    @Getter String startup = null;
    String exec = null;
    @Getter String xmx;
    @Getter String pathName;
    @Getter String javaVersion = "default";
    @Getter int port = 0;

    public JVMConfig(){
        // Empty constructor
    }

    public JVMConfig(String name, JVMExecutor.Mods type, String xms, String startup, String exec, String xmx, String pathName, String javaVersion, int port){
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
