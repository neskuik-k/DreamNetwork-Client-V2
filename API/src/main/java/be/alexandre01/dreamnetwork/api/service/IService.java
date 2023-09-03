package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;

import java.util.Optional;

public interface IService {
    int getId();
    long getProcessID();
    int getPort();
    Optional<String> getUniqueCharactersID();
    String getXmx();
    String getXms();
    IJVMExecutor.Mods getType();
    IClient getClient();
    boolean isConnected();
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
    ExecutorCallbacks getExecutorCallbacks();
}
