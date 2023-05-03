package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class ClientManager implements IClientManager {
    private final HashMap<Integer, Client> clientByPort = new HashMap<>();
    @Getter private final HashMap<String, IClient> clients = new HashMap<>();
    @Getter private final HashMap<ChannelHandlerContext, IClient> clientsByConnection = new HashMap<>();
    @Getter private final ArrayList<IClient> devTools = new ArrayList<>();

    private final Core main;
    @Getter @Setter
    private Client proxy;
    public ClientManager(Core core){
        this.main = core;
    }
    @Override
    public Client registerClient(Client client){
        if(client.isDevTool()){
            client.setClientManager(this);
            clientsByConnection.put(client.getChannelHandlerContext(),client);
            devTools.add(client);
            return client;
        }

        Console.print("PORT >> " + client.getPort(), Level.FINE);
        Console.print("PORTS >> "+ Arrays.toString(JVMExecutor.servicePort.keySet().toArray()),Level.FINE);
        IService jvmService = JVMExecutor.servicePort.get(client.getPort());
        if(jvmService == null){
            Console.print("A service tried to connect on the port " + client.getPort()+" but there is a problem",Level.SEVERE);
            client.getChannelHandlerContext().channel().close();
            return null;
        }

        clientByPort.put(client.getPort(),client);

        //System.out.println(client.getPort());



        clients.put(jvmService.getFullName(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        client.setJvmService(jvmService);
        jvmService.setClient(client);
        client.setClientManager(this);
        return client;
    }

    @Override
    public IClient getClient(String processName){
        return clients.get(processName);
    }
    @Override
    public IClient getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }


}
