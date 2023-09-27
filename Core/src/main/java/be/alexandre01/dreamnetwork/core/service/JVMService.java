package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
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
    private Client client;
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
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if(screen != null){
            Console.fine("Stop screen");
            screen.destroy(true);
        }

        if(getJvmExecutor().getType() == JVMExecutor.Mods.DYNAMIC){
            Config.removeDir("/runtimes/"+ getJvmExecutor().getBundleData().getName() + "/"+ getJvmExecutor().getName()+"/"+getJvmExecutor().getName()+"-"+getId());
        }

        if(executorCallbacks != null){
            if(!isConnected()){
                if(executorCallbacks.onFail != null){
                    executorCallbacks.onFail.whenFail();
                }
            }
            if(executorCallbacks.onStop != null){
                executorCallbacks.onStop.whenStop(this);
            }
        }


        if(client != null){
            client.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER);
            //close with delay to let the server send the response
            //set delay of 1 seconds without lock the thread
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.getChannelHandlerContext().close();
            }).start();
        }else{
            process.destroy();
        }
        return CompletableFuture.completedFuture(true);
    }




    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<Boolean> kill() {
        process.destroy();
        process.destroyForcibly();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Optional<ExecutorCallbacks> restart(){
        if(usedConfig == null){

            return restart(jvmExecutor.getConfig());
        }
        return restart(usedConfig);
    }
    @Override
    public Optional<ExecutorCallbacks> restart(IConfig iConfig){
        if(screen != null){
            Console.fine("Restart screen");
            screen.destroy(true);
        }

        if(client != null){
            client.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER);
            client.getChannelHandlerContext().close();
        }else{
            process.destroy();
        }
        //removeService();
        return Optional.ofNullable(getJvmExecutor().startServer(iConfig, new ExecutorCallbacks()));
    }

    @Override
    public synchronized void removeService() {
        jvmExecutor.removeService(this);
    }

    @Override
    public void setClient(IClient client) {
        this.client = (Client) client;
    }

    @Override
    public Optional<ExecutorCallbacks> getExecutorCallbacks() {
        return Optional.ofNullable(executorCallbacks);
    }
}
