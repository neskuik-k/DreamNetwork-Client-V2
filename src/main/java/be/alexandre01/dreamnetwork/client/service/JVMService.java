package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import lombok.Builder;
import lombok.Data;
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

    private Screen screen = null;

    @Override
    public void stop(){
        if(screen != null){
            screen.destroy();
        }

        if(client != null){
            client.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER);
            client.getChannelHandlerContext().close();
        }else{
            process.destroy();
        }


    }

    @Override
    public void restart() {

    }

    @Override
    public void sendData() {

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
    public void removeService() {
        jvmExecutor.removeService(id);
    }

    @Override
    public void setClient(IClient client) {
        this.client = (Client) client;
    }
}
