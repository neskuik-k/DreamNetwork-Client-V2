package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;

public interface IService {

    int getId();
    int getPort();
    String getXmx();
    String getXms();
    IJVMExecutor.Mods getType();
    IClient getClient();
    IJVMExecutor getJvmExecutor();

    String getFullName();
    Process getProcess();
    IScreen getScreen();
    IConfig getUsedConfig();
    void setScreen(IScreen screen);
    void stop();
    void restart();
    void restart(IConfig config);
    void kill();

    void removeService();

    void setClient(IClient client);
}
