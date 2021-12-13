package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannel;
import be.alexandre01.dreamnetwork.client.connection.core.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.client.connection.core.players.Player;
import be.alexandre01.dreamnetwork.client.connection.core.players.ServicePlayersManager;
import be.alexandre01.dreamnetwork.client.connection.core.players.ServicePlayersObject;
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
import java.util.UUID;

public class BaseResponse extends CoreResponse {
    private final Client client;
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
            if(message.getHeader().equals("cData") && message.getChannel() != null){
                if(this.client.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    DNChannel channel = this.client.getChannelManager().getChannel(message.getChannel());
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
                System.out.println("Est ce que je m'aime aussi ?");
                System.out.println(message.getChannel());
                System.out.println(this.client.getChannelManager().getClientsRegistered().keySet());
                if(this.client.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    System.out.println("Hmm ouais");
                    DNChannel channel = this.client.getChannelManager().getChannel(message.getChannel());
                    System.out.println("Le channel bien sur  : " + channel.getName());
                    System.out.println(message +" il faut ça ?");
                    message.set("value", channel.getData(message.getString("key")));
                    System.out.println("To >> "+ message);
                    ChannelPacket channelPacket = new ChannelPacket(message);
                    channelPacket.createResponse(message,client,"cAsk");
                }
            }
            if(message.getHeader().equals("channel") && message.getChannel() != null){
                if(this.client.getChannelManager().getClientsRegistered().containsKey(message.getChannel())){
                    final Collection<ClientManager.Client> clients = this.client.getChannelManager().clientsRegistered.get(message.getChannel());
                    if(!clients.isEmpty()){
                        boolean resend = true;

                        if(this.client.getChannelManager().getDontResendsData().contains(client)){
                            resend = false;
                        }
                        /*Console.debugPrint("NotEmptyGetClients");
                        Console.debugPrint("NotEmptyGetClients "+ this.client.getChannelManager().clientsRegistered.get(message.getChannel()));*/
                        for(ClientManager.Client c : this.client.getChannelManager().getClientsRegistered().get(message.getChannel())){
                            if(!resend && c == client){
                                continue;
                            }
                            c.getCoreHandler().writeAndFlush(message,c);
                        }
                    }
                }
            }
        }
        System.out.println("Message reçu : " + message);
        if(message.hasRequest()){
            System.out.println("Has Request");
            if(message.hasProvider()){
                System.out.println("Has Provider");
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
                    String server = (String) message.getInRoot("RETRANS");
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
                    System.out.println("register channel !  " + message.getString("CHANNEL"));
                    this.client.getChannelManager().registerClientToChannel(client,message.getString("CHANNEL"),message.contains("RESEND") && message.getBoolean("RESEND"));
                    break;
                case CORE_UNREGISTER_CHANNEL:
                    this.client.getChannelManager().unregisterClientToChannel(client,message.getString("CHANNEL"));
                    break;
                case CORE_UPDATE_PLAYER:
                    ServicePlayersManager s = this.client.getServicePlayersManager();
                    int id = message.getInt("ID");
                    if(!s.getPlayersMap().containsKey(id)){
                        if(message.contains("P")){
                            Player player;
                            if(message.contains("U")){
                                 player = new Player(id,message.getString("P"), UUID.fromString(message.getString("U")));
                            }else {
                                 player = new Player(id,message.getString("P"));
                            }
                            s.registerPlayer(player);
                        }else {
                            return;
                        }
                    }

                    if (message.contains("S")) {
                        s.udpatePlayerServer(id,message.getString("S"));
                    }
                    break;
                case CORE_REMOVE_PLAYER:
                    s = this.client.getServicePlayersManager();
                    id = message.getInt("ID");

                    s.unregisterPlayer(id);
                    break;
                case CORE_ASK_DATA:
                    s = this.client.getServicePlayersManager();
                    String type = message.getString("TYPE");
                    String mode = message.getString("MODE");
                    if(mode.equals("ALWAYS")){
                        boolean bo = s.getWantToBeInformed().containsKey(client);

                        s.removeUpdatingClient(client);
                        if(!bo){
                            if(type.equalsIgnoreCase("PLAYERS")){
                                client.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS,s.getPlayersMap().values().toArray());

                                s.getObjects().put(client,new ServicePlayersObject(client, ServicePlayersManager.DataType.PLAYERS_LIST));
                                s.getWantToBeDirectlyInformed().add(s.getObject(client));
                                return;
                            }
                            if(type.equalsIgnoreCase("PCOUNT")){
                                s.getObjects().put(client,new ServicePlayersObject(client, ServicePlayersManager.DataType.PLAYERS_COUNT));
                                client.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS_COUNT,s.getPlayersMap().values().toArray());
                                s.getWantToBeDirectlyInformed().add(s.getObject(client));
                            }
                        }

                        return;
                    }else {
                        if(!message.contains("TIME")){
                            return;
                        }
                        if(type.equalsIgnoreCase("PLAYERS")){
                            long time =  message.getLong("TIME");
                            s.removeUpdatingClient(client);
                            s.addUpdatingClient(client,time, ServicePlayersManager.DataType.PLAYERS_LIST);
                            return;
                        }
                        if(type.equalsIgnoreCase("PCOUNT")){
                            long time =  message.getLong("TIME");
                            s.removeUpdatingClient(client);
                            s.addUpdatingClient(client,time, ServicePlayersManager.DataType.PLAYERS_LIST);
                        }


                    }
                    break;

            }
        }
    }
    
    
}
