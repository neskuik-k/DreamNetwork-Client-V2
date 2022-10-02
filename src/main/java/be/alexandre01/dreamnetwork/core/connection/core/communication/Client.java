package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.request.RequestManager;
import be.alexandre01.dreamnetwork.core.connection.request.generated.devtool.DefaultDevToolRequest;
import be.alexandre01.dreamnetwork.core.connection.request.generated.proxy.DefaultBungeeRequest;
import be.alexandre01.dreamnetwork.core.connection.request.generated.spigot.DefaultSpigotRequest;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Client implements IClient {
    private int port;
    private boolean isDevTool = false;
    private JVMContainer.JVMType jvmType = null;
    private String info;
    private ChannelHandlerContext channelHandlerContext;

    private final RequestManager requestManager;
    private CoreHandler coreHandler;
    private IService jvmService;
    private ClientManager clientManager;
    private ArrayList<String> accessChannels = new ArrayList<>();

    @Builder
    public Client(int port, String info, CoreHandler coreHandler, ChannelHandlerContext ctx, JVMContainer.JVMType jvmType, boolean isDevTool) {
        this.port = port;
        this.info = info;
        this.isDevTool = isDevTool;
        this.coreHandler = coreHandler;
        this.channelHandlerContext = ctx;
        this.jvmType = jvmType;
        requestManager = new RequestManager(this);
        if (jvmType == null) {
            switch (info.split("-")[0]) {
                case "SPIGOT":
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
            }
        }
        if (isDevTool) {
            requestManager.getRequestBuilder().addRequestBuilder(new DefaultDevToolRequest());
        }
    }


    @Override
    public void writeAndFlush(Message message) {
        coreHandler.writeAndFlush(message, this);
    }


    @Override
    public void writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        coreHandler.writeAndFlush(message, listener, this);
    }
}
