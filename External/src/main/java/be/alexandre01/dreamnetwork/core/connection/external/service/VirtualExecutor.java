package be.alexandre01.dreamnetwork.core.connection.external.service;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import io.netty.channel.ChannelHandlerContext;


import java.io.File;
import java.util.Collection;
import java.util.Optional;

public class VirtualExecutor implements IJVMExecutor {
    ConfigData configData;


    public VirtualExecutor(ConfigData configData, BundleData bundle) {
        this.configData = configData;
    }

    private Optional<IClient> externalTool;

    @Override
    public ExecutorCallbacks startServer() {
        return startServer(":this:", new ExecutorCallbacks());
    }

    @Override
    public ExecutorCallbacks startServer(String profile) {
        return startServer(profile, new ExecutorCallbacks());
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig) {
        return startServer(jvmConfig, new ExecutorCallbacks());
    }

    @Override
    public ExecutorCallbacks startServer(String profile, ExecutorCallbacks callbacks) {
        externalTool.ifPresent(client -> {
            VirtualService virtualService = new VirtualService(getPort(), null, this);
            sendStartCallBack(client, callbacks, virtualService, profile);
        });
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig, ExecutorCallbacks callbacks) {
        externalTool.ifPresent(client -> {
            VirtualService virtualService = new VirtualService(getPort(), null, this);
            sendStartCallBack(client, callbacks, virtualService, jvmConfig);
        });
        return callbacks;
    }

    private void sendStartCallBack(IClient client, ExecutorCallbacks callbacks, VirtualService virtualService,Object o){
        DNCallback.single(client.getRequestManager().getRequest(RequestType.CORE_START_SERVER, getFullName(),o), new TaskHandler() {
            @Override
            public void onCallback() {
                if (hasType(TaskType.CUSTOM)) {
                    String custom = getCustomType();
                    if (custom.equalsIgnoreCase("STARTED")) {
                        callbacks.onStart.whenStart(virtualService);
                        return;
                    }
                    if (custom.equalsIgnoreCase("LINKED")) {
                        // create a client

                        callbacks.onConnect.whenConnect(virtualService, null);
                        return;
                    }
                }
                if (hasType(TaskType.IGNORED) || hasType(TaskType.REFUSED) || hasType(TaskType.FAILED)) {
                    callbacks.onFail.whenFail();
                }
            }
        }).send();
    }

    @Override
    public ExecutorCallbacks startServers(int i) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServers(int i, IConfig jvmConfig) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServers(int i, String profile) {
        return null;
    }

    @Override
    public void removeService(IService service) {

    }

    @Override
    public IService getService(Integer i) {
        return null;
    }

    @Override
    public Collection<IService> getServices() {
        return null;
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isConfig() {
        return false;
    }

    @Override
    public boolean isFixedData() {
        return false;
    }

    @Override
    public File getFileRootDir() {
        return null;
    }

    @Override
    public IConfig getConfig() {
        return null;
    }

    @Override
    public IStartupConfig getStartupConfig() {
        return null;
    }

    @Override
    public Mods getType() {
        return null;
    }

    @Override
    public String getXms() {
        return null;
    }

    @Override
    public String getStartup() {
        return null;
    }

    @Override
    public String getExecutable() {
        return null;
    }

    @Override
    public String getXmx() {
        return null;
    }

    @Override
    public String getPathName() {
        return null;
    }

    @Override
    public String getJavaVersion() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public boolean hasExecutable() {
        return false;
    }

    @Override
    public BundleData getBundleData() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public ExecType getExecType() {
        return null;
    }

    @Override
    public InstallationLinks getInstallLink() {
        return null;
    }

    @Override
    public IProfiles getJvmProfiles() {
        return null;
    }
}
