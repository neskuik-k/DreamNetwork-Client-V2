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
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;


import java.io.File;
import java.util.*;

public class VirtualExecutor  implements IJVMExecutor {
    ConfigData configData;
    BundleData bundleData;
    HashMap<Integer,IService> serviceList = new HashMap<>();

    @Getter IClient externalTool;



    public VirtualExecutor(ConfigData configData, BundleData bundle,IClient externalTool){
        this.configData = configData;
        this.bundleData = bundle;
        this.externalTool = externalTool;
    }


    public VirtualService createOrGetService(Integer id){
        if(serviceList.containsKey(id)){
            return (VirtualService) serviceList.get(id);
        }
        VirtualService virtualService = new VirtualService(null, this);
        serviceList.put(id,virtualService);
        return virtualService;
    }



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
        VirtualService virtualService = new VirtualService( null, this);
        sendStartCallBack(externalTool, callbacks, virtualService, profile);
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(ExecutorCallbacks callbacks){
        return startServer(":this:",callbacks);
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig, ExecutorCallbacks callbacks) {
        VirtualService virtualService = new VirtualService( null, this);
        sendStartCallBack(externalTool, callbacks, virtualService, jvmConfig);
        return callbacks;
    }

    private void sendStartCallBack(IClient client, ExecutorCallbacks callbacks, VirtualService virtualService,Object o){
        System.out.println("Sending start callback to "+ getTrueFullName().get());
        DNCallback.multiple(client.getRequestManager().getRequest(RequestType.CORE_START_SERVER, getTrueFullName().get(),o), new TaskHandler() {
            Integer id;
            @Override
            public void onCallback() {
                if (hasType(TaskType.CUSTOM)) {
                    String custom = getCustomType();
                    if (custom.equalsIgnoreCase("STARTED")) {
                        Message message = getResponse();
                        if(message.contains("name")){
                            String name = message.getString("name");
                            String splittedName = name.split("-")[1];
                            if(splittedName.matches("[0-9]+")){
                                id = Integer.parseInt(splittedName);
                                serviceList.put(id,virtualService);
                                virtualService.setExecutorCallbacks(callbacks);
                            }
                        }
                        callbacks.onStart.whenStart(virtualService);
                        return;
                    }
                }
                if (hasType(TaskType.IGNORED) || hasType(TaskType.REFUSED) || hasType(TaskType.FAILED)) {
                    callbacks.onFail.whenFail();
                }
            }
        }).send();

        // link
    }

    @Override
    public ExecutorCallbacks startServers(int i) {
        return startServers(i, ":this:");
    }

    @Override
    public ExecutorCallbacks startServers(int i, IConfig jvmConfig) {
        ExecutorCallbacks callbacks = new ExecutorCallbacks();
        for (int j = 0; j < i; j++) {
            startServer(jvmConfig,callbacks);
        }
        return callbacks;
    }

    @Override
    public ExecutorCallbacks startServers(int i, String profile) {
        ExecutorCallbacks callbacks = new ExecutorCallbacks();
        for (int j = 0; j < i; j++) {
            startServer(profile,callbacks);
        }
        return callbacks;
    }

    @Override
    public void removeService(IService service) {
        serviceList.remove(service.getId());
        IJVMExecutor.super.removeService(service);
    }

    @Override
    public IService getService(Integer i) {
        return serviceList.get(i);
    }

    @Override
    public Collection<IService> getServices() {
        return serviceList.values();
    }

    @Override
    public boolean isProxy() {
        return bundleData.getJvmType().equals(IContainer.JVMType.PROXY);
    }

    @Override
    public String getName() {
        return configData.getName();
    }

    @Override
    public boolean isConfig() {
        return true;
    }

    @Override
    public boolean isFixedData() {
        return false;
    }

    @Override  // to optional
    public File getFileRootDir() {
        return null;
    }

    @Override //to optional
    public IConfig getConfig() {
        return null;
    }

    @Override // to optional
    public IStartupConfig getStartupConfig() {
        return null;
    }

    @Override
    public Mods getType() {
        return configData.getType();
    }

    @Override
    public String getXms() {
        return configData.getXms();
    }

    @Override
    public String getStartup() {
        return configData.getStartup();
    }

    @Override
    public String getExecutable() {
        return configData.getExecutable();
    }

    @Override
    public String getXmx() {
        return configData.getXmx();
    }

    @Override // to optional
    public String getPathName() {
        return null;
    }

    @Override
    public String getJavaVersion() {
        return configData.getJavaVersion();
    }

    @Override
    public int getPort() {
        return configData.getPort();
    }

    @Override
    public boolean hasExecutable() {
        return true;
    }

    @Override
    public BundleData getBundleData() {
        return bundleData;
    }

    @Override
    public String getFullName() {
        return getBundleData().getName()+"/"+getName();
    }

    @Override
    public Optional<ExecType> getExecType() {
        if(!getInstallLink().isPresent()) return Optional.empty();
        try {
            return Optional.of(ExecType.valueOf(getInstallLink().get().getExecType().name()));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<InstallationLinks> getInstallLink() {
        try {
            return Optional.of(InstallationLinks.valueOf(configData.getInstallInfo()));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<IProfiles> getJvmProfiles() {
        return Optional.empty();
    }

    public Optional<String> getTrueFullName(){
        if(getBundleData().getVirtualName().isPresent()){
            return Optional.of(getBundleData().getVirtualName().get() + "/" + getName());
        }
        return Optional.empty();
    }


}
