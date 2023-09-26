package be.alexandre01.dreamnetwork.core.connection.external.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import lombok.Setter;

import java.util.Optional;

public class VirtualService implements IService {
    @Setter int id = -1;
    @Setter int port = 0;
    IClient client;
    VirtualExecutor executor;

    @Setter ExecutorCallbacks executorCallbacks;




    public VirtualService(IClient client, VirtualExecutor executor){
        this.client = client;
        this.executor = executor;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getProcessID() {
        return -1;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Optional<String> getUniqueCharactersID() {
        return Optional.empty();
    }

    @Override
    public String getXmx() {
        return "N/A";
    }

    @Override
    public String getXms() {
        return "N/A";
    }

    @Override
    public IJVMExecutor.Mods getType() {
        return null;
    }

    @Override
    public IClient getClient() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public VirtualExecutor getJvmExecutor() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getFullName(boolean withBundlePath) {
        return null;
    }

    @Override
    public Process getProcess() {
        return null;
    }

    @Override
    public IScreen getScreen() {
        return null;
    }

    @Override
    public IConfig getUsedConfig() {
        return null;
    }

    @Override
    public void setScreen(IScreen screen) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void restart(IConfig config) {

    }

    @Override
    public void kill() {

    }

    @Override
    public void removeService() {

    }

    @Override
    public void setClient(IClient client) {

    }

    @Override
    public Optional<ExecutorCallbacks> getExecutorCallbacks() {
        return Optional.ofNullable(executorCallbacks);
    }
}
