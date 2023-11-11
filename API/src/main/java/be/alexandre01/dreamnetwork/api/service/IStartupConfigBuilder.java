package be.alexandre01.dreamnetwork.api.service;



public interface IStartupConfigBuilder {

    static IStartupConfigBuilder builder() {
        return new ConfigBuilder();
    }

    static IStartupConfigBuilder builder(IStartupConfig iConfig) {
        return new ConfigBuilder(new ConfigBuilder().buildFrom(iConfig));
    }

    IStartupConfigBuilder name(String name);

    IStartupConfigBuilder pathName(String pathName);

    IStartupConfigBuilder type(IExecutor.Mods type);

    IStartupConfigBuilder xms(String Xms);

    IStartupConfigBuilder xmx(String Xmx);

    IStartupConfigBuilder port(int port);

    IStartupConfigBuilder proxy(boolean proxy);

    IStartupConfigBuilder exec(String exec);

    IStartupConfigBuilder startup(String startup);

    IStartupConfigBuilder javaVersion(String javaVersion);

    IStartupConfig build();
    IConfig build(boolean save);
    IStartupConfig buildFrom(IStartupConfig config);

    IStartupConfig buildFrom(ConfigData config);
}
