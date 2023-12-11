package be.alexandre01.dreamnetwork.core.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IService;
import lombok.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter @Builder
public class JVMService implements IService {
    private int id;
    private int port;
    private ServiceClient client;
    private JVMExecutor jvmExecutor;
    private Process process;

    private IExecutor.Mods type;

    private String xmx;
    private String xms;

    private String uniqueCharactersID;
    private String customName;
    private long processID;
    private IScreen screen = null;
    private int indexingId;
    private String fullIndexedName;

    public IConfig usedConfig;
    @Getter(AccessLevel.NONE) public ExecutorCallbacks executorCallbacks;

    CompletableFuture<Boolean> stopFuture = new CompletableFuture<>();



    @Override
    public int getIndexingId() {
        return indexingId;
    }

    @Override
    public Optional<String> getCustomName() {
        return Optional.ofNullable(customName);
    }

    @Override
    public String getFullIndexedName() {
        return fullIndexedName;
    }

    @Override
    public Optional<String> getUniqueCharactersID() {
        return Optional.ofNullable(uniqueCharactersID);
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

    @Override
    public String getName(){
        return customName != null ? customName : getJvmExecutor().getName();
    }


    @Override @Synchronized
    public CompletableFuture<Boolean> stop(){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        safeStop().whenComplete((aBoolean, throwable) -> {
            if(!aBoolean) {
                removeService();
                completableFuture.complete(false);
                return;
            }
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                int trying = 0;
                @Override
                public void run() {
                    trying ++;
                    if(!process.isAlive() || trying > 10){
                        if(trying > 10){
                            Console.fine("Process is not dead, killing it");
                            process.destroy();
                            process.destroyForcibly();
                        }
                        scheduledExecutorService.shutdown();
                        removeService();
                        completableFuture.complete(true);
                    }
                }
            }, 1000, 1000, java.util.concurrent.TimeUnit.MILLISECONDS);
        });
        return completableFuture;
    }


    public CompletableFuture<Boolean> safeStop(){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        if(client == null){
            completableFuture.complete(false);
            return completableFuture;
        }

        Console.print(Colors.PURPLE+"Safe stopping the process "+getFullName());
        DNCallback.single(client.getRequestManager().getRequest(RequestType.CORE_STOP_SERVER, getFullName()), new TaskHandler() {
            @Override
            public void onAccepted() {
                completableFuture.complete(true);
            }
            @Override
            public void onFailed() {
                completableFuture.complete(false);
            }
        }).send();
        return completableFuture;
    }




    public void setClient(ServiceClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<Boolean> kill() {
        process.destroy();
        process.destroyForcibly();
        removeService();
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
                    ExecutorCallbacks c = getJvmExecutor().startService(iConfig);
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
