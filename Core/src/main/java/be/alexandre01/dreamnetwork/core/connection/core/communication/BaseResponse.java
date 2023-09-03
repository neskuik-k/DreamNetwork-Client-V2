package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.request.RequestPacket;
import be.alexandre01.dreamnetwork.core.connection.external.ExecutorData;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.connection.request.RequestType.*;

public class BaseResponse extends CoreResponse {
    private final Core core;
    public BaseResponse(){
        this.core = Core.getInstance();
        addRequestInterceptor(CORE_START_SERVER, (message, ctx, c) -> {
            IJVMExecutor startExecutor = this.core.getJvmContainer().tryToGetJVMExecutor(message.getString("SERVERNAME"));
            if (startExecutor == null) {
                return;
            }
            startExecutor.startServer();
        });

        addRequestInterceptor(CORE_STOP_SERVER, (message, ctx, c) -> {
            String[] stopServerSplitted = message.getString("SERVERNAME").split("-");
            IJVMExecutor stopExecutor = this.core.getJvmContainer().tryToGetJVMExecutor(stopServerSplitted[0]);
            if (stopExecutor == null) {
                return;
            }
            Console.fine("Stopping server " + stopServerSplitted[0] + " with id " + stopServerSplitted[1]);
            stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).stop();
            //stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).removeService();
        });

        addRequestInterceptor(SERVER_EXECUTE_COMMAND,(message, ctx, c) -> {
            IClient cmdClient = this.core.getClientManager().getClient(message.getString("SERVERNAME"));
            if (cmdClient != null) {
                cmdClient.getRequestManager().sendRequest(SERVER_EXECUTE_COMMAND, message.getString("CMD"));
            }

            String server = (String) message.getInRoot("RETRANS");
            this.core.getClientManager().getClient(server).writeAndFlush(message);
        });

        addRequestInterceptor(CORE_RETRANSMISSION,(message, ctx, c) -> {
            String server = (String) message.getInRoot("RETRANS");
            this.core.getClientManager().getClient(server).writeAndFlush(message);
        });

        addRequestInterceptor(DEV_TOOLS_VIEW_CONSOLE_MESSAGE,(message, ctx, c) -> {
            ScreenManager.instance.getScreens().get(message.getString("SERVERNAME")).getDevToolsReading().add(c);
        });

        addRequestInterceptor(DEV_TOOLS_SEND_COMMAND,(message, ctx, c) -> {
            boolean b = Boolean.parseBoolean(message.getString("TYPE"));
            String[] serv = message.getString("SERVERNAME").split("-");
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

        addRequestInterceptor(CORE_REGISTER_CHANNEL,(message, ctx, c) -> {
            this.core.getChannelManager().registerClientToChannel(c, message.getString("CHANNEL"), message.contains("RESEND") && message.getBoolean("RESEND"));
        });

        addRequestInterceptor(CORE_UNREGISTER_CHANNEL,(message, ctx, c) -> {
            this.core.getChannelManager().unregisterClientToChannel(c, message.getString("CHANNEL"));
        });

        addRequestInterceptor(CORE_REGISTER_EXTERNAL_EXECUTORS, (message, ctx, client) -> {
            System.out.println("Wow j'ai reçu cette requete");
            List<ExecutorData> executor = message.getList("executors", ExecutorData.class);

            executor.forEach(jvmExecutor -> {
                System.out.println(jvmExecutor.name);
            });
        });
    }
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, IClient client) throws Exception {
        //Console.debugPrint(message);
        Console.printLang("connection.core.communication.enteringRequest", Level.FINE);
        ChannelPacket receivedPacket = new ChannelPacket(message);
        IDNChannel dnChannel = this.core.getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
            dnChannel.received(receivedPacket);
            if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                for (AChannelPacket.DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                    dnChannelInterceptor.received(receivedPacket);
                }
            }
        }
        if(message.getHeader()!=null){
            if(message.getHeader().equals("cData") && message.getChannel() != null){
                if(this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    IDNChannel channel = this.core.getChannelManager().getChannel(message.getChannel());
                    if(message.contains("init")){
                        if(message.getBoolean("init")){
                            String key = message.getString("key");
                            if(!dnChannel.getObjects().containsKey(key)){
                                dnChannel.getObjects().put(key, message.get("value"));
                            }
                        }
                    }
                    if(!message.contains("update")){
                        channel.storeData(message.getString("key"),message.get("value"),client);
                    }else {
                        channel.storeData(message.getString("key"),message.get("value"), message.getBoolean("update"),client);
                    }

                }
            }
            if(message.getHeader().equals("cAsk") && message.getChannel() != null){
                if(this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    IDNChannel channel = this.core.getChannelManager().getChannel(message.getChannel());
                    Console.print("Le channel bien sur  : " + channel.getName(),Level.FINE);
                    message.set("value", channel.getData(message.getString("key")));
                    Console.print("To >> "+ message, Level.FINE);
                    ChannelPacket channelPacket = new ChannelPacket(message);
                    channelPacket.createResponse(message,client,"cAsk");
                }
            }
            if(message.getHeader().equals("channel") && message.getChannel() != null){
                if(this.core.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    final Collection<IClient> clients = this.core.getChannelManager().getClientsRegistered().get(message.getChannel());
                    if(!clients.isEmpty()){
                        boolean resend = true;

                        if(this.core.getChannelManager().getDontResendsData().contains(client)){
                            resend = false;
                        }
                        /*Console.debugPrint("NotEmptyGetClients");
                        Console.debugPrint("NotEmptyGetClients "+ this.client.getChannelManager().clientsRegistered.get(message.getChannel()));*/
                        for(IClient c : this.core.getChannelManager().getClientsRegistered().get(message.getChannel())){
                            if(!resend && c == client){
                                continue;
                            }
                            c.getCoreHandler().writeAndFlush(message,c);
                        }
                    }
                }
            }
        }

        if(message.hasRequest()){
            if(message.hasProvider()){
                if(message.getProvider().equals("core")){
                    RequestPacket request = client.getRequestManager().getRequest(message.getMessageID());
                    if(request != null)
                        request.getRequestFutureResponse().onReceived(receivedPacket);
                }
            }
            //RequestInfo request = message.getRequest();
        }
    }
}

