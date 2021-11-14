package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannel;
import be.alexandre01.dreamnetwork.client.connection.core.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.client.connection.request.RequestPacket;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

public class BaseResponse extends CoreResponse {
    private Client client;
    public BaseResponse(){
        this.client = Client.getInstance();
    }
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, ClientManager.Client client) throws Exception {
        Console.debugPrint(message);
        ChannelPacket receivedPacket = new ChannelPacket(message);
        DNChannel dnChannel = this.client.getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
            dnChannel.received(receivedPacket);
            if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                for (DNChannel.DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                    dnChannelInterceptor.received(receivedPacket);
                }
            }
        }
        if(message.getHeader()!=null){
            if(message.getHeader().equals("channel") && message.getChannel() != null){
                Console.debugPrint("NotNull +"+ message.getChannel() );
                if(this.client.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    Console.debugPrint("HasChannel");
                    final Collection<ClientManager.Client> clients = this.client.getChannelManager().clientsRegistered.get(message.getChannel());
                    Console.debugPrint("GetClients");
                    if(!clients.isEmpty()){
                        Console.debugPrint("NotEmptyGetClients");
                        Console.debugPrint("NotEmptyGetClients "+ this.client.getChannelManager().clientsRegistered.get(message.getChannel()));
                        for(ClientManager.Client c : this.client.getChannelManager().getClientsRegistered().get(message.getChannel())){
                            Console.debugPrint("C=>> "+c);
                            c.getCoreHandler().writeAndFlush(message,c);
                        }
                    }
                }
            }
        }
        if(message.hasRequest()){
            if(message.hasProvider()){
                if(message.getProvider().equals("core")){
                    RequestPacket request = client.getRequestManager().getRequest(message.getRequestID());
                    if(request != null)
                        request.getRequestFutureResponse().onReceived(receivedPacket);
                }
            }
            switch (message.getRequest()){
                case CORE_START_SERVER:
                    JVMExecutor startExecutor = this.client.getJvmContainer().getJVMExecutor(message.getString("SERVERNAME"), JVMContainer.JVMType.SERVER);
                    if(startExecutor == null){
                        return;
                    }
                    startExecutor.startServer();
                    break;
                case CORE_STOP_SERVER:
                    String[] stopServerSplitted = message.getString("SERVERNAME").split("-");
                    JVMExecutor stopExecutor =  this.client.getJvmContainer().getJVMExecutor(stopServerSplitted[0], JVMContainer.JVMType.SERVER);
                    if(stopExecutor == null){
                        return;
                    }
                    stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).stop();
                    break;
                case SPIGOT_EXECUTE_COMMAND:
                    System.out.println("EXECUTE COMMAND");
                    ClientManager.Client cmdClient =  this.client.getClientManager().getClient(message.getString("SERVERNAME"));
                    if(cmdClient != null){
                        cmdClient.getRequestManager().sendRequest(RequestType.SPIGOT_EXECUTE_COMMAND,message.getString("CMD"));
                    }
                case CORE_RETRANSMISSION:
                    String server = message.getString("RETRANS");
                    this.client.getClientManager().getClient(server).writeAndFlush(message);
                    break;
                case DEV_TOOLS_VIEW_CONSOLE_MESSAGE:
                    ScreenManager.instance.getScreens().get(message.getString("SERVERNAME")).getDevToolsReading().add(client);
                    break;
                case DEV_TOOLS_SEND_COMMAND:
                    boolean b = Boolean.parseBoolean(message.getString("TYPE"));
                    String[] serv = message.getString("SERVERNAME").split("-");
                    String cmd = message.getString("CMD");

                    if(b){
                        JVMExecutor j =  this.client.getJvmContainer().jvmExecutorsProxy.get(serv[0]);
                        if(j == null)
                            return;
                        JVMService jvmService = j.getService(Integer.valueOf(serv[1]));
                        if(jvmService.getClient() != null){
                            jvmService.getClient().getRequestManager().sendRequest(RequestType.BUNGEECORD_EXECUTE_COMMAND,cmd);
                        }
                    }else {
                        JVMExecutor j =  this.client.getJvmContainer().jvmExecutorsServers.get(serv[0]);
                        if(j == null)
                            return;
                        JVMService jvmService = j.getService(Integer.valueOf(serv[1]));
                        if(jvmService.getClient() != null){
                            jvmService.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_EXECUTE_COMMAND,cmd);
                        }
                    }
                    break;
                case CORE_REGISTER_CHANNEL:
                    this.client.getChannelManager().registerClientToChannel(client,message.getString("CHANNEL"));
                    break;
                case CORE_UNREGISTER_CHANNEL:
                    this.client.getChannelManager().unregisterClientToChannel(client,message.getString("CHANNEL"));
                    break;

            }
        }
    }
    
    
}
