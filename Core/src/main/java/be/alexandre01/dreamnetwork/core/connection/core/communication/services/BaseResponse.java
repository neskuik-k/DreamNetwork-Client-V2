package be.alexandre01.dreamnetwork.core.connection.core.communication.services;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.AbstractRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.service.bundle.BService;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleInfo;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.core.connection.core.interceptors.StartTask;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualExecutor;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import com.google.common.collect.Table;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.connection.core.request.RequestType.*;

public class BaseResponse extends CoreResponse {
    private final Core core;
    HashMap<String, CoreResponse.RequestInterceptor> tasks = AbstractRequestManager.getTasks();
    RequestInterceptor start = StartTask.get();
    public BaseResponse() {
        this.core = Core.getInstance();

        addRequestInterceptor(CORE_START_SERVER, (message, ctx, c) -> {
            start.onRequest(message, ctx, c);
        });

        addRequestInterceptor(CORE_STOP_SERVER, (message, ctx, c) -> {
            String[] stopServerSplitted = message.getString("SERVERNAME").split("-");
            Optional<IJVMExecutor> stopExecutor = this.core.getJvmContainer().tryToGetJVMExecutor(stopServerSplitted[0]);
            if (!stopExecutor.isPresent()) {
                return;
            }
            Console.fine("Stopping server " + stopServerSplitted[0] + " with id " + stopServerSplitted[1]);
            CompletableFuture<Boolean> future = stopExecutor.get().getService(Integer.valueOf(stopServerSplitted[1])).stop();

            message.getCallback().ifPresent(receiver -> {
                future.whenComplete((aBoolean, throwable) -> {
                   if(aBoolean){
                       receiver.send(TaskHandler.TaskType.ACCEPTED);
                   }else {
                       receiver.send(TaskHandler.TaskType.FAILED);
                   }
                });
            });
            //stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).removeService();
        });

        addRequestInterceptor(CORE_RESTART_SERVER, (message, ctx, c) -> {
            String[] stopServerSplitted = message.getString("SERVERNAME").split("-");
            Optional<IJVMExecutor> stopExecutor = this.core.getJvmContainer().tryToGetJVMExecutor(stopServerSplitted[0]);
            if (!stopExecutor.isPresent()) {
                return;
            }
            Console.fine("Stopping server " + stopServerSplitted[0] + " with id " + stopServerSplitted[1]);
            CompletableFuture<IService.RestartResult> future = stopExecutor.get().getService(Integer.valueOf(stopServerSplitted[1])).restart();

            message.getCallback().ifPresent(receiver -> {
                future.whenComplete((result, throwable) -> {
                    if(result.isSuccess()){
                        receiver.send(TaskHandler.TaskType.ACCEPTED);
                    }else {
                        receiver.send(TaskHandler.TaskType.FAILED);
                    }
                });
            });
            //stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).removeService();
        });

        addRequestInterceptor(SERVER_EXECUTE_COMMAND, (message, ctx, c) -> {
            AServiceClient cmdClient = this.core.getClientManager().getClient(message.getString("SERVERNAME"));
            if (cmdClient != null) {
                cmdClient.getRequestManager().sendRequest(SERVER_EXECUTE_COMMAND, message.getString("CMD"));
            }

            String server = (String) message.getInRoot("RETRANS");
            this.core.getClientManager().getClient(server).writeAndFlush(message);
        });

        addRequestInterceptor(CORE_RETRANSMISSION, (message, ctx, c) -> {
            String server = (String) message.getInRoot("RETRANS");
            this.core.getClientManager().getClient(server).writeAndFlush(message);
        });

        /*addRequestInterceptor(DEV_TOOLS_VIEW_CONSOLE_MESSAGE, (message, ctx, c) -> {
            ScreenManager.instance.getScreens().get(message.getString("DATA")).getDevToolsReading().add((AServiceClient) c);
        });*/


        addRequestInterceptor(DEV_TOOLS_SEND_COMMAND, (message, ctx, c) -> {
            String[] serv = message.getString("SERVICE").split("-");
            String cmd = message.getString("CMD");
            /*
            if (b) {

                IJVMExecutor j = this.core.getJvmContainer().jvmExecutorsProxy.get(serv[0]);
                if (j == null)
                    return;
                IService jvmService = j.getService(Integer.valueOf(serv[1]));
                if (jvmService.getClient() != null) {
                    jvmService.getClient().getRequestManager().sendRequest(BUNGEECORD_EXECUTE_COMMAND, cmd);
                }
            } else {
                IJVMExecutor j = this.core.getJvmContainer().jvmExecutorsServers.get(serv[0]);
                if (j == null)
                    return;
                IService jvmService = j.getService(Integer.valueOf(serv[1]));
                if (jvmService.getClient() != null) {
                    jvmService.getClient().getRequestManager().sendRequest(SPIGOT_EXECUTE_COMMAND, cmd);
                }
            }*/
        });

        addRequestInterceptor(CORE_REGISTER_CHANNEL, (message, ctx, c) -> {
            this.core.getChannelManager().registerClientToChannel((AServiceClient) c, message.getString("CHANNEL"), message.contains("RESEND") && message.getBoolean("RESEND"));
        });

        addRequestInterceptor(CORE_UNREGISTER_CHANNEL, (message, ctx, c) -> {
            this.core.getChannelManager().unregisterClientToChannel((AServiceClient) c, message.getString("CHANNEL"));
        });

        addRequestInterceptor(CORE_REGISTER_EXTERNAL_EXECUTORS, (message, ctx, client) -> {
            System.out.println("Wow j'ai reçu cette requete");
            ArrayList<ConfigData> executor = message.getList("executors", ConfigData.class);



            System.out.println("J'ai reçu");
            /*for (Test test : executor) {
                System.out.println("Num " + test.age);
                System.out.println(test.name);
                System.out.println(test.e);
            }*/
            if(!(client instanceof ExternalClient)){
               return;
            }
            executor.forEach(configData -> {
                BundleData virtualBundle = null;

                Table<ExternalClient, String,BundleData> virtualBundles = DNCoreAPI.getInstance().getBundleManager().getVirtualBundles();

                if(!virtualBundles.containsRow(client) || !virtualBundles.row((ExternalClient) client).containsKey(configData.getBundleName())){
                    virtualBundle = new BundleData(configData.getBundleName(), new IBundleInfo() {
                        @Override
                        public ArrayList<BService> getBServices() {
                            return null;
                        }

                        @Override
                        public ArrayList<BService> getServices() {
                            return null;
                        }

                        @Override
                        public String getName() {
                            return configData.getBundleName();
                        }

                        @Override
                        public IContainer.JVMType getType() {
                            return configData.getJvmType();
                        }

                        @Override
                        public ExecType getExecType() {
                            return ExecType.SERVER;
                        }

                        @Override
                        public File getFile() {
                            return null;
                        }


                    });
                    virtualBundle.setVirtual(true);
                    String name = configData.getBundleName();
                    virtualBundle.setVirtualName(Optional.of(name));

                    DNCoreAPI.getInstance().getBundleManager().addBundleData(virtualBundle);
                    DNCoreAPI.getInstance().getBundleManager().addVirtualBundleData(virtualBundle, (ExternalClient) client);
                    // a new name has been potentially created and setted on the bundle

                    System.out.println(""+client);
                    DNCoreAPI.getInstance().getBundleManager().getBundlesNamesByTool().put((ExternalClient) client,name,virtualBundle.getName());
                    CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);
                }

                if(virtualBundle == null){
                    virtualBundle = DNCoreAPI.getInstance().getBundleManager().getVirtualBundles().get(client,configData.getBundleName());
                }

                VirtualExecutor virtualExecutor = new VirtualExecutor(configData,virtualBundle, (ExternalClient) client);
                virtualBundle.getExecutors().put(virtualExecutor.getName(), virtualExecutor);
                DNCoreAPI.getInstance().getContainer().getJVMExecutors().add(virtualExecutor);

                if(virtualBundle.getJvmType() == IContainer.JVMType.PROXY){
                    DNCoreAPI.getInstance().getContainer().getProxiesExecutors().add(virtualExecutor);
                }else {
                    DNCoreAPI.getInstance().getContainer().getServersExecutors().add(virtualExecutor);
                }
            });
        });
    }

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
        //Console.debugPrint(message);
        Console.printLang("connection.core.communication.enteringRequest", Level.FINE);


        IDNChannel dnChannel = this.core.getChannelManager().getChannel(message.getChannel());
        if (dnChannel != null) {
            ChannelPacket receivedPacket = new ChannelPacket(message);
            dnChannel.received(receivedPacket);
            if (!dnChannel.getDnChannelInterceptors().isEmpty()) {
                for (AChannelPacket.DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()) {
                    dnChannelInterceptor.received(receivedPacket);
                }
            }
        }
        if (message.getHeader() != null) {
            if (message.getHeader().equals("cData") && message.getChannel() != null) {
                if (this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())) {
                    IDNChannel channel = this.core.getChannelManager().getChannel(message.getChannel());
                    if (message.contains("init")) {
                        if (message.getBoolean("init")) {
                            String key = message.getString("key");
                            if (!dnChannel.getObjects().containsKey(key)) {
                                dnChannel.getObjects().put(key, message.get("value"));
                            }
                        }
                    }
                    if (!message.contains("update")) {
                        channel.storeData(message.getString("key"), message.get("value"), client);
                    } else {
                        channel.storeData(message.getString("key"), message.get("value"), message.getBoolean("update"), client);
                    }

                }
            }
            if (message.getHeader().equals("cAsk") && message.getChannel() != null) {
                if (this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())) {
                    IDNChannel channel = this.core.getChannelManager().getChannel(message.getChannel());
                    Console.print("Le channel bien sur  : " + channel.getName(), Level.FINE);
                    message.set("value", channel.getData(message.getString("key")));
                    Console.print("To >> " + message, Level.FINE);
                    ChannelPacket channelPacket = new ChannelPacket(message);
                    channelPacket.createResponse(message, client, "cAsk");
                }
            }
            if (message.getHeader().equals("channel") && message.getChannel() != null) {
                if (this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())) {
                    final Collection<AServiceClient> clients = this.core.getChannelManager().getClientsRegistered().get(message.getChannel());
                    if (!clients.isEmpty()) {
                        boolean resend = true;

                        if (this.core.getChannelManager().getDontResendsData().contains(client)) {
                            resend = false;
                        }
                        /*Console.debugPrint("NotEmptyGetClients");
                        Console.debugPrint("NotEmptyGetClients "+ this.client.getChannelManager().clientsRegistered.get(message.getChannel()));*/
                        for (AServiceClient c : this.core.getChannelManager().getClientsRegistered().get(message.getChannel())) {
                            if (!resend && c == client) {
                                continue;
                            }
                            c.getCoreHandler().writeAndFlush(message, c);
                        }
                    }
                }
            }
        }

        // if core send data and received callback

        if (message.containsKeyInRoot("RID")) {
                    int id = (int) message.getInRoot("RID");
                    ICallbackManager callbackManager = DNCoreAPI.getInstance().getCallbackManager();
                    callbackManager.getHandlerOf(id).ifPresent(handler -> {
                        handler(message, handler);
                    });
        }

        // verifier si single
        /*message.getCallback().ifPresent(receiver -> {
            receiver.
        });*/

            //RequestInfo request = message.getRequest();
    }

    private void handler(Message message, TaskHandler handler){
        handler.setupHandler(message);
        handler.onCallback();

        switch (handler.getTaskType()) {
            case ACCEPTED:
                handler.onAccepted();
                break;
            case REJECTED:
                handler.onRejected();
                break;
            case IGNORED:
                handler.onIgnored();
                break;
            case FAILED:
                handler.onFailed();
                handler.destroy();
                break;
            case TIMEOUT:
                handler.onFailed();
                handler.onTimeout();
                handler.destroy();
                break;
            case CUSTOM:
                handler.onCustom(handler.getCustomType());
                break;
        }
        if(handler.isSingle()){
            handler.destroy();
        }
    }
}

