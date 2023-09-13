package be.alexandre01.dreamnetwork.core.connection.external.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;

import java.util.Optional;

public class VirtualService implements IService {
    int id;
    int port;
    IClient client;
    VirtualExecutor executor;




    public VirtualService(int port, IClient client, VirtualExecutor executor){
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public long getProcessID() {
        return 0;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public Optional<String> getUniqueCharactersID() {
        return Optional.empty();
    }

    @Override
    public String getXmx() {
        return null;
    }

    @Override
    public String getXms() {
        return null;
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
    public ExecutorCallbacks getExecutorCallbacks() {
        return null;
    }
}
