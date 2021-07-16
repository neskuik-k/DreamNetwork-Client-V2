package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.request.RequestManager;
import be.alexandre01.dreamnetwork.client.connection.request.generated.proxy.DefaultBungeeRequest;
import be.alexandre01.dreamnetwork.client.connection.request.generated.spigot.DefaultSpigotRequest;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;

public class ClientManager {
    private final HashMap<Integer,Client> clientByPort = new HashMap<>();
    private final HashMap<String,Client> clients = new HashMap<>();
    private final HashMap<ChannelHandlerContext,Client> clientsByConnection = new HashMap<>();
    private be.alexandre01.dreamnetwork.client.Client main;
    @Getter private Client proxy;
    public ClientManager(be.alexandre01.dreamnetwork.client.Client client){
        this.main = client;
    }
    public Client registerClient(Client client){
        clientByPort.put(client.port,client);
        System.out.println("PORT >> "+ client.getPort());
        System.out.println("PORTS >> "+ Arrays.toString(JVMExecutor.servicePort.keySet().toArray()));
        JVMService jvmService = JVMExecutor.servicePort.get(client.getPort());
        clients.put(jvmService.getJvmExecutor().getName()+"-"+ jvmService.getId(),client);
        clientsByConnection.put(client.getChannelHandlerContext(),client);
        client.jvmService = jvmService;
        client.clientManager = this;
        return client;
    }

    public Client getClient(String processName){
        return clients.get(processName);
    }
    public Client getClient(ChannelHandlerContext ctx){
        return clientsByConnection.get(ctx);
    }
    @Data
    public static class Client{
        private int port;
        private JVMContainer.JVMType jvmType = null;
        private String info;
        private ChannelHandlerContext channelHandlerContext;
        private final RequestManager requestManager;
        private CoreHandler coreHandler;
        private JVMService jvmService;
        private ClientManager clientManager;
        @Builder
        public Client(int port, String info, CoreHandler coreHandler, ChannelHandlerContext ctx, JVMContainer.JVMType jvmType){
            this.port = port;
            this.info = info;
            this.coreHandler = coreHandler;
            this.channelHandlerContext = ctx;
            this.jvmType = jvmType;
            requestManager = new RequestManager(this);
            if(jvmType == null){
                System.out.println(info.split("-")[0]);
                switch (info.split("-")[0]){
                    case "SPIGOT":
                        this.jvmType = JVMContainer.JVMType.SERVER;
                        requestManager.getRequestBuilder().addRequestBuilder(new DefaultSpigotRequest());
                        break;
                    case "BUNGEE":
                        this.jvmType = JVMContainer.JVMType.PROXY;
                        be.alexandre01.dreamnetwork.client.Client.getInstance().getClientManager().proxy = this;
                        requestManager.getRequestBuilder().addRequestBuilder(new DefaultBungeeRequest());
                        break;
                }
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
