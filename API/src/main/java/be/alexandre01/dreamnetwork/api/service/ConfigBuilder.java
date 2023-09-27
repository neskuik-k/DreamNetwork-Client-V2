package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.DNUtils;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 00:42
*/

@NoArgsConstructor
public class ConfigBuilder implements IStartupConfigBuilder {
    protected String name;
    protected String pathName;
    protected IJVMExecutor.Mods type;
    protected String Xms;
    protected String Xmx;
    protected int port;
    protected String exec;
    protected String startup;
    protected String javaVersion;
    protected List<String> deployers;
    private boolean proxy;



    public ConfigBuilder(IConfig config){
        if(config == null){
            return;
        }
        this.name = config.getName();
        this.pathName = config.getPathName();
        this.type = config.getType();
        this.Xms = config.getXms();
        this.Xmx = config.getXmx();
        this.port = config.getPort();
        this.exec = config.getExecutable();
        this.startup = config.getStartup();
        this.javaVersion = config.getJavaVersion();
        this.deployers = config.getDeployers();
    }

    @Override
    public ConfigBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ConfigBuilder pathName(String pathName) {
        this.pathName = pathName;
        return this;
    }



    @Override
    public ConfigBuilder type(IJVMExecutor.Mods type) {
        this.type = type;
        return this;
    }

    @Override
    public ConfigBuilder xms(String Xms) {
        this.Xms = Xms;
        return this;
    }

    @Override
    public ConfigBuilder xmx(String Xmx) {
        this.Xmx = Xmx;
        return this;
    }

    @Override
    public ConfigBuilder port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public ConfigBuilder proxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public ConfigBuilder exec(String exec) {
        this.exec = exec;
        return this;
    }

    @Override
    public ConfigBuilder startup(String startup) {
        this.startup = startup;
        return this;
    }

    @Override
    public ConfigBuilder javaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
        return this;
    }

    @Override
    public IStartupConfig build() {

        IStartupConfig j =  DNCoreAPI.getInstance().getJVMUtils().createStartupConfig(pathName,name,true);
        j.setType(type);
        j.setXms(Xms);
        j.setXmx(Xmx);
        j.setPort(port);
        j.setProxy(proxy);
        j.setJavaVersion(javaVersion);
        j.setExecutable(exec);
        j.setStartup(startup);
        j.setDeployers(deployers);
        return j;
    }

    @Override
    public IConfig build(boolean save) {
        if(save){
            return build();
        }
        IConfig j =  DNCoreAPI.getInstance().getJVMUtils().createConfig();
        j.setType(type);
        j.setXms(Xms);
        j.setXmx(Xmx);
        j.setPort(port);
        j.setJavaVersion(javaVersion);
        j.setExecutable(exec);
        j.setStartup(startup);
        j.setDeployers(deployers);
        return j;
    }

    @Override
    public IStartupConfig buildFrom(IStartupConfig config) {
        name = config.getName();
        pathName = config.getPathName();
        IStartupConfig j = build();
        if (j.getName() == null)
            j.setName(config.getName());
        if (j.getPathName() == null)
            j.setPathName(config.getPathName());
        if (j.getType() == null)
            j.setType(config.getType());
        if (j.getXms() == null)
            j.setXms(config.getXms());
        if (j.getXmx() == null)
            j.setXmx(config.getXmx());
        if (j.getPort() == 0)
            j.setPort(config.getPort());
        if (j.getJavaVersion() == null)
            j.setJavaVersion(config.getJavaVersion());
        if (j.getExecutable() == null)
            j.setExecutable(config.getExecutable());
        if (j.getStartup() == null)
            j.setStartup(config.getStartup());
        if (j.getDeployers() == null)
            j.setDeployers(config.getDeployers());

        j.setProxy(config.isProxy());
        return j;
    }
}
