package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.service.interfaces.IService;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class JVMService implements IService {
    private int id;
    private int port;
    private ClientManager.Client client;
    private JVMExecutor jvmExecutor;
    private Process process;

    @Override
    public void stop(){
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

    public void setClient(ClientManager.Client client) {
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
}
