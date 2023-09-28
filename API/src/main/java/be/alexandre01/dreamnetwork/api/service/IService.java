package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    String getFullName(boolean withBundlePath);
    Process getProcess();
    IScreen getScreen();
    IConfig getUsedConfig();

    void setScreen(IScreen screen);
    CompletableFuture<Boolean> stop();
    CompletableFuture<Boolean> kill();
    Optional<ExecutorCallbacks> restart();
    Optional<ExecutorCallbacks> restart(IConfig config);

    void removeService();

    void setClient(IClient client);
    Optional<ExecutorCallbacks> getExecutorCallbacks();
}
