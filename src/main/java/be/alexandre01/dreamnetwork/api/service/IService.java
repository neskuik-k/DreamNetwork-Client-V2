package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;

public interface IService {

    int getId();
    int getPort();
    IClient getClient();
    IJVMExecutor getJvmExecutor();
    Process getProcess();
    Screen getScreen();
    void stop();
    void restart();

    void sendData();

    void kill();

    void removeService();

    void setClient(IClient client);
}
