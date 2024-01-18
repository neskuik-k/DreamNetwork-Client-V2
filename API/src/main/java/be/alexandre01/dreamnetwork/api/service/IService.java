package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IService {
    int getId();

    int getIndexingId();

    String getFullIndexedName();

    long getProcessID();
    int getPort();
    Optional<String> getUniqueCharactersID();

    Optional<String> getCustomName();
    String getXmx();
    String getXms();
    IExecutor.Mods getType();
    AServiceClient getClient();
    boolean isConnected();
    IExecutor getExecutor();

    String getFullName();
    String getFullName(boolean withBundlePath);

    String getName();
    Process getProcess();
    IScreen getScreen();
    IConfig getUsedConfig();

    void setScreen(IScreen screen);
    CompletableFuture<Boolean> stop();
    CompletableFuture<Boolean> kill();
    CompletableFuture<RestartResult> restart();
    CompletableFuture<RestartResult> restart(IConfig config);

    void removeService();

    void setClient(AServiceClient client);
    Optional<ExecutorCallbacks> getExecutorCallbacks();

    long getStartTime();
    default long getElapsedTime(){
        return System.currentTimeMillis() - getStartTime();
    }

    List<Runnable> getStopsCallbacks();
    default void onStop(Runnable callbackStop){
        getStopsCallbacks().add(callbackStop);
    }

    default void removeStopCallback(Runnable callbackStop){
        getStopsCallbacks().remove(callbackStop);
    }


    @AllArgsConstructor
    static class RestartResult{
        @Getter private final boolean success;
        private final ExecutorCallbacks executorCallbacks;

        public Optional<ExecutorCallbacks> getExecutorCallbacks(){
            return Optional.ofNullable(executorCallbacks);
        }
    }
}
