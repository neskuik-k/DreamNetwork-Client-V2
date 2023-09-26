package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.core.requests.ClientRequestManager;
import be.alexandre01.dreamnetwork.core.connection.core.requests.devtool.DefaultDevToolRequest;
import be.alexandre01.dreamnetwork.core.connection.core.requests.proxy.DefaultBungeeRequest;
import be.alexandre01.dreamnetwork.core.connection.core.requests.spigot.DefaultSpigotRequest;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.Pack;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Client implements IClient {
    private String name;
    private int port;
    private boolean isExternalTool = false;
    private boolean isExternalService = false;
    private JVMContainer.JVMType jvmType = null;
    private String info;
    private ChannelHandlerContext channelHandlerContext;

    private final ClientRequestManager requestManager;
    private ICoreHandler coreHandler;
    private IService jvmService;
    private IClientManager clientManager;
    private ArrayList<String> accessChannels = new ArrayList<>();



    @Builder
    public Client(int port, String info, CoreHandler coreHandler, ChannelHandlerContext ctx, JVMContainer.JVMType jvmType, boolean isExternalTool,boolean isExternalService) {
        this.port = port;
        this.info = info;
        this.isExternalTool = isExternalTool;
        this.isExternalService = isExternalService;
        this.coreHandler = coreHandler;
        this.channelHandlerContext = ctx;
        this.jvmType = jvmType;
        this.name = "Unknown-Client="+ ctx.channel().remoteAddress().toString()+":"+port;


        requestManager = new ClientRequestManager(this);
        Console.fine("Client : "+info);
        if (jvmType == null) {
            switch (info.split("-")[0]) {
                case "SPIGOT":
                    this.jvmType = JVMContainer.JVMType.SERVER;
                    requestManager.getRequestBuilder().addRequestBuilder(new DefaultSpigotRequest());
                    break;
                case "SPONGE":
                    this.jvmType = JVMContainer.JVMType.SERVER;
                    requestManager.getRequestBuilder().addRequestBuilder(new DefaultSpigotRequest());
                    break;
                case "BUNGEE":
                    this.jvmType = JVMContainer.JVMType.PROXY;
                    Core core = Core.getInstance();
                    if (core.getClientManager().getProxy() == null) {
                        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                        service.scheduleAtFixedRate(() -> {
                            core.getBundleManager().onProxyStarted();
                            service.shutdown();
                        }, 3, 3, TimeUnit.SECONDS);
                    }
                    core.getClientManager().setProxy(this);
                    requestManager.getRequestBuilder().addRequestBuilder(new DefaultBungeeRequest());
                    break;
                case "VELOCITY":
                    this.jvmType = JVMContainer.JVMType.PROXY;
                    Core c = Core.getInstance();
                    if (c.getClientManager().getProxy() == null) {
                        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                        service.scheduleAtFixedRate(() -> {
                            c.getBundleManager().onProxyStarted();
                            service.shutdown();
                        }, 3, 3, TimeUnit.SECONDS);
                    }
                    c.getClientManager().setProxy(this);
                    requestManager.getRequestBuilder().addRequestBuilder(new DefaultBungeeRequest());
                    break;
            }
        }
        if (isExternalTool) {
            requestManager.getRequestBuilder().addRequestBuilder(new DefaultDevToolRequest());
        }
    }
    public void setJvmService(IService iService){
        this.name = "ServiceClient="+ iService.getFullName();
        this.jvmService = iService;
    }

    @Override
    public Packet writeAndFlush(Message message) {
        coreHandler.writeAndFlush(message, this);
        return message.toPacket(this);
    }


    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        coreHandler.writeAndFlush(message, listener, this);
        return message.toPacket(this);
    }
}
