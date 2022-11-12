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
    Process getProcess();
    IScreen getScreen();

    void setScreen(IScreen screen);
    void stop();
    void restart();
    void kill();

    void removeService();

    void setClient(IClient client);
}
