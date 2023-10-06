package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * The {@code ClientManager} class implements the {@code IClientManager} interface and provides methods for managing clients.
 */
public class ClientManager implements IClientManager {
    private final HashMap<Integer, AServiceClient> clientByPort = new HashMap<>();
    @Getter private final HashMap<String, AServiceClient> clients = new HashMap<>();
    @Getter private final HashMap<ChannelHandlerContext, AServiceClient> clientsByConnection = new HashMap<>();
    @Getter private final HashMap<String, AServiceClient> externalTools = new HashMap<>();

    private final Core main;
    @Getter @Setter
    private ServiceClient proxy;
    public ClientManager(Core core){
        this.main = core;
    }


    @Override
    public AServiceClient registerClient(AServiceClient client){
        if(client.isExternalTool()){
            client.setClientManager(this);
            clientsByConnection.put(client.getChannelHandlerContext(),client);
            return client;
        }
        IService jvmService = null;
        Console.print("PORT >> " + client.getPort(), Level.FINE);
        Console.print("PORTS >> "+ Arrays.toString(JVMExecutor.servicePort.keySet().toArray()),Level.FINE);
        if(!client.isExternalService()){
            if(JVMExecutor.servicePort.isEmpty()){
                Console.print("A service tried to connect with the port " + client.getPort()+" but there is a problem (ARRAY EMPTY)",Level.SEVERE);
                client.getChannelHandlerContext().channel().close();
                return null;
            }
             jvmService = JVMExecutor.servicePort.get(client.getPort());
            if(jvmService == null){
                Console.print("A service tried to connect with the port " + client.getPort()+" but there is a problem",Level.SEVERE);
                client.getChannelHandlerContext().channel().close();
                return null;
            }
            clientByPort.put(client.getPort(),client);
        }else {
            jvmService = client.getJvmService();
            if(jvmService == null){
                Console.print("A external service tried to connect but there is a problem",Level.SEVERE);
                client.getChannelHandlerContext().channel().close();
                return null;
            }
        }
        //System.out.println(client.getPort());



        clients.put(jvmService.getFullName(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        if(client instanceof ServiceClient){
            ((ServiceClient) client).setJvmService(jvmService);
        }
        jvmService.setClient(client);
        client.setClientManager(this);
        return client;
    }

    @Override
    public AServiceClient getClient(String processName){
        return clients.get(processName);
    }
    @Override
    public AServiceClient getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }


}
