package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IService;
import lombok.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
@Builder
public class JVMService implements IService {
    private int id;
    private int port;
    private ServiceClient client;
    private JVMExecutor jvmExecutor;
    private Process process;

    private IJVMExecutor.Mods type;

    private String xmx;
    private String xms;

    private Optional<String> uniqueCharactersID;
    private long processID;
    private IScreen screen = null;

    public IConfig usedConfig;
    @Getter(AccessLevel.NONE) public ExecutorCallbacks executorCallbacks;

    CompletableFuture<Boolean> stopFuture = new CompletableFuture<>();

    @Override
    public Optional<String> getUniqueCharactersID() {
        return uniqueCharactersID;
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public String getFullName() {
        return getJvmExecutor().getFullName()+"-"+getId();
    }

    @Override
    public String getFullName(boolean withBundlePath) {
        if(withBundlePath){
            return getFullName();
        }
        return getJvmExecutor().getName()+"-"+getId();
    }

    @Override @Synchronized
    public CompletableFuture<Boolean> stop(){
        removeService();
        return CompletableFuture.completedFuture(true);
    }




    public void setClient(ServiceClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<Boolean> kill() {
        process.destroy();
        process.destroyForcibly();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<RestartResult> restart(){
        if(usedConfig == null){
            return restart(jvmExecutor.getConfig());
        }
        return restart(usedConfig);
    }
    @Override
    public CompletableFuture<RestartResult> restart(IConfig iConfig){
        CompletableFuture<RestartResult> completableFuture = new CompletableFuture<>();
        if(screen != null){
            Console.fine("Restart screen");
            screen.destroy(true);
        }
        stop().whenComplete((aBoolean, throwable) -> {
                if(aBoolean){
                    System.out.println("Stop succeed");
                    ExecutorCallbacks c = getJvmExecutor().startServer(iConfig);
                    c.whenStart(new ExecutorCallbacks.ICallbackStart() {
                        @Override
                        public void whenStart(IService service) {
                            completableFuture.complete(new RestartResult(true,c));
                        }
                    });
                }else{
                    System.out.println("Stop failed");
                    completableFuture.complete(new RestartResult(false,null));
                }
            });
           return completableFuture;
    }

    @Override
    public synchronized void removeService() {
        jvmExecutor.removeService(this);
    }

    @Override
    public void setClient(AServiceClient client) {
        this.client = (ServiceClient) client;
    }

    @Override
    public Optional<ExecutorCallbacks> getExecutorCallbacks() {
        return Optional.ofNullable(executorCallbacks);
    }
}
