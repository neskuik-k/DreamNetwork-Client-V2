package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class ClientManager implements IClientManager {
    private final HashMap<Integer, Client> clientByPort = new HashMap<>();
    @Getter private final HashMap<String, Client> clients = new HashMap<>();
    @Getter private final HashMap<ChannelHandlerContext, Client> clientsByConnection = new HashMap<>();
    @Getter private final ArrayList<Client> devTools = new ArrayList<>();

    private be.alexandre01.dreamnetwork.client.Client main;
    @Getter @Setter
    private Client proxy;
    public ClientManager(be.alexandre01.dreamnetwork.client.Client client){
        this.main = client;
    }
    @Override
    public Client registerClient(Client client){
        if(client.isDevTool()){
            client.setClientManager(this);
            clientsByConnection.put(client.getChannelHandlerContext(),client);
            devTools.add(client);
            return client;
        }
        clientByPort.put(client.getPort(),client);
        Console.print("PORT >> " + client.getPort(), Level.FINE);
        Console.print("PORTS >> "+ Arrays.toString(JVMExecutor.servicePort.keySet().toArray()),Level.FINE);
        JVMService jvmService = JVMExecutor.servicePort.get(client.getPort());
        clients.put(jvmService.getJvmExecutor().getName()+"-"+ jvmService.getId(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        client.setJvmService(jvmService);
        jvmService.setClient(client);
        client.setClientManager(this);
        return client;
    }

    @Override
    public Client getClient(String processName){
        return clients.get(processName);
    }
    @Override
    public Client getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }

}
