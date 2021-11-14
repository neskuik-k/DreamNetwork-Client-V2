package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.request.RequestManager;
import be.alexandre01.dreamnetwork.client.connection.request.generated.devtool.DefaultDevToolRequest;
import be.alexandre01.dreamnetwork.client.connection.request.generated.proxy.DefaultBungeeRequest;
import be.alexandre01.dreamnetwork.client.connection.request.generated.spigot.DefaultSpigotRequest;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ClientManager {
    private final HashMap<Integer, Client> clientByPort = new HashMap<>();
    @Getter private final HashMap<String, Client> clients = new HashMap<>();
    @Getter private final HashMap<ChannelHandlerContext, Client> clientsByConnection = new HashMap<>();
    @Getter private final ArrayList<Client> devTools = new ArrayList<>();

    private be.alexandre01.dreamnetwork.client.Client main;
    @Getter private Client proxy;
    public ClientManager(be.alexandre01.dreamnetwork.client.Client client){
        this.main = client;
    }
    public Client registerClient(ClientManager.Client client){
        if(client.isDevTool){
            client.clientManager = this;
            clientsByConnection.put(client.getChannelHandlerContext(),client);
            devTools.add(client);
            return client;
        }
        clientByPort.put(client.port,client);
        Console.print("PORT >> " + client.getPort(), Level.FINE);
        Console.print("PORTS >> "+ Arrays.toString(JVMExecutor.servicePort.keySet().toArray()),Level.FINE);
        JVMService jvmService = JVMExecutor.servicePort.get(client.getPort());
        clients.put(jvmService.getJvmExecutor().getName()+"-"+ jvmService.getId(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        client.jvmService = jvmService;
        jvmService.setClient(client);
        client.clientManager = this;
        return client;
    }

    public Client getClient(String processName){
        return clients.get(processName);
    }
    public Client getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }

    @Getter @Setter
    public static class Client{
        private int port;
        private boolean isDevTool = false;
        private JVMContainer.JVMType jvmType = null;
        private String info;
        private ChannelHandlerContext channelHandlerContext;
        private final RequestManager requestManager;
        private CoreHandler coreHandler;
        private JVMService jvmService;
        private ClientManager clientManager;
        private ArrayList<String> accessChannels = new ArrayList<>();

        @Builder
        public Client(int port, String info, CoreHandler coreHandler, ChannelHandlerContext ctx, JVMContainer.JVMType jvmType,boolean isDevTool){
            this.port = port;
            this.info = info;
            this.isDevTool = isDevTool;
            this.coreHandler = coreHandler;
            this.channelHandlerContext = ctx;
            this.jvmType = jvmType;
            requestManager = new RequestManager(this);
            if(jvmType == null){
                switch (info.split("-")[0]){
                    case "SPIGOT":
                    case "SPONGE":
                        this.jvmType = JVMContainer.JVMType.SERVER;
                        requestManager.getRequestBuilder().addRequestBuilder(new DefaultSpigotRequest());
                        break;
                    case "BUNGEE":
                        this.jvmType = JVMContainer.JVMType.PROXY;
                        be.alexandre01.dreamnetwork.client.Client client = be.alexandre01.dreamnetwork.client.Client.getInstance();
                        if(client.getClientManager().proxy == null){
                            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                            service.scheduleAtFixedRate(() -> {
                                client.getBundleManager().onProxyStarted();
                                service.shutdown();
                            },3,3, TimeUnit.SECONDS);

                        }
                        client.getClientManager().proxy = this;
                        requestManager.getRequestBuilder().addRequestBuilder(new DefaultBungeeRequest());
                        break;
                }
            }
            if(isDevTool){
                requestManager.getRequestBuilder().addRequestBuilder(new DefaultDevToolRequest());
            }
        }

        public void writeAndFlush(Message message){
            coreHandler.writeAndFlush(message,this);
        }

        public void writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener){
            coreHandler.writeAndFlush(message,listener,this);
        }
    }
}
