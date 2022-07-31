package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;

public interface IService {

    int getId();
    int getPort();
    IClient getClient();
    IJVMExecutor getJvmExecutor();
    Process getProcess();
    IScreen getScreen();

    void setScreen(IScreen screen);
    void stop();

    void kill();

    void removeService();

    void setClient(IClient client);
}
