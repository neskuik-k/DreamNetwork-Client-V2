package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMStartupConfig;

public interface IStartupConfigBuilder {

    static IStartupConfigBuilder builder() {
        return new JVMStartupConfig.Builder();
    }

    IStartupConfigBuilder name(String name);

    IStartupConfigBuilder pathName(String pathName);

    IStartupConfigBuilder type(JVMExecutor.Mods type);

    IStartupConfigBuilder xms(String Xms);

    IStartupConfigBuilder xmx(String Xmx);

    IStartupConfigBuilder port(int port);

    IStartupConfigBuilder proxy(boolean proxy);

    IStartupConfigBuilder exec(String exec);

    IStartupConfigBuilder startup(String startup);

    IStartupConfigBuilder javaVersion(String javaVersion);

    IStartupConfig build();
}
