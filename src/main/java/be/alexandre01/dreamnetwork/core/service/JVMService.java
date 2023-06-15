package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.console.Console;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    private IScreen screen = null;

    public IConfig usedConfig;
    public ExecutorCallbacks executorCallbacks;


    @Override
    public String getFullName() {
        return getJvmExecutor().getFullName()+"-"+getId();
    }

    @Override
    public synchronized void stop(){
        if(screen != null){
            Console.fine("Stop screen");
            screen.destroy();
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
    }




    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void kill() {
        process.destroy();
        process.destroyForcibly();
    }

    @Override
    public void restart(){
        if(usedConfig == null){
            restart(jvmExecutor.getConfig());
            return;
        }
        restart(usedConfig);
    }
    @Override
    public void restart(IConfig iConfig){
        if(screen != null){
            Console.fine("Restart screen");
            screen.destroy();
        }

        if(client != null){
            client.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER);
            client.getChannelHandlerContext().close();
        }else{
            process.destroy();
        }
        //removeService();
        getJvmExecutor().startServer(iConfig);
    }

    @Override
    public synchronized void removeService() {
        jvmExecutor.removeService(this);
    }

    @Override
    public void setClient(IClient client) {
        this.client = (Client) client;
    }
}
