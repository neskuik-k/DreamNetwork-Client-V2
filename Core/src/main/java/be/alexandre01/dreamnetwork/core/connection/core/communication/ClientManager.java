package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;
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
    @Getter private final HashMap<String, AServiceClient> serviceClients = new HashMap<>();
    @Getter private final HashMap<ChannelHandlerContext, UniversalConnection> clientsByConnection = new HashMap<>();
    @Getter private final HashMap<String, ExternalClient> externalTools = new HashMap<>();

    private final Core main;
    @Getter @Setter
    private ServiceClient proxy;
    public ClientManager(Core core){
        this.main = core;
    }


    @Override
    public AServiceClient registerClient(AServiceClient client){
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
            jvmService = client.getService();
            if(jvmService == null){
                Console.print("A external service tried to connect but there is a problem",Level.SEVERE);
                client.getChannelHandlerContext().channel().close();
                return null;
            }
        }
        //System.out.println(client.getPort());



        serviceClients.put(jvmService.getFullName(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        if(client instanceof ServiceClient){
            ((ServiceClient) client).setService(jvmService);
        }
        jvmService.setClient(client);
        client.setClientManager(this);
        return client;
    }

    @Override
    public AServiceClient getClient(String processName){
        return serviceClients.get(processName);
    }
    @Override
    public <T> T getClient(ChannelHandlerContext ctx, Class<T> tClass){
        UniversalConnection i = clientsByConnection.get(ctx);
        if(tClass.isInstance(i)){
            return tClass.cast(i);
        }
        return null;
    }

    @Override
    public UniversalConnection getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }

    @Override
    public AServiceClient getServiceClient(ChannelHandlerContext ctx){
        return getClient(ctx, AServiceClient.class);
    }

}
