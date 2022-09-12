package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.cms.PasswordRecipientId;

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



    @Override
    public synchronized void stop(){
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

    public synchronized void restart() {

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
    public synchronized void removeService() {
        jvmExecutor.removeService(this);
    }

    @Override
    public void setClient(IClient client) {
        this.client = (Client) client;
    }
}
