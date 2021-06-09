package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.request.RequestManager;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jdk.jfr.internal.JVM;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

public class ClientManager {
    private final HashMap<Integer,Client> clientByPort = new HashMap<>();
    private final HashMap<String,Client> clients = new HashMap<>();
    private be.alexandre01.dreamnetwork.client.Client main;

    public ClientManager(be.alexandre01.dreamnetwork.client.Client client){
        this.main = client;
    }
    public Client registerClient(Client client){
        clientByPort.put(client.port,client);
        JVMService jvmService = JVMExecutor.servicePort.get(client.getPort());
        clients.put(jvmService.getJvmExecutor().getName()+"-"+ jvmService.getId(),client);
        client.jvmService = jvmService;
        return client;
    }

    public Client getClient(String processName){
        return clients.get(processName);
    }
    @Data
    public static class Client{
        private int port;
        private JVMContainer.JVMType jvmType;
        private String info;
        private Channel channel;
        private final RequestManager requestManager;
        private CoreHandler coreHandler;
        private JVMService jvmService;

        @Builder
        public Client(int port, String info, CoreHandler coreHandler,Channel channel){
            this.port = port;
            this.info = info;
            this.coreHandler = coreHandler;
            requestManager = new RequestManager(this);
            if(jvmType == null){
                switch (info.split("-")[0]){
                    case "SPIGOT":
                        jvmType = JVMContainer.JVMType.SERVER;
                        break;
                    case "BUNGEECORD":
                        jvmType = JVMContainer.JVMType.PROXY;
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
