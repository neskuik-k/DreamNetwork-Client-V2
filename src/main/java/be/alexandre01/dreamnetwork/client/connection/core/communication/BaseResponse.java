package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannel;
import be.alexandre01.dreamnetwork.client.connection.request.ReceivedPacket;
import be.alexandre01.dreamnetwork.client.connection.request.RequestPacket;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public class BaseResponse extends CoreResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, ClientManager.Client client) throws Exception {
        Console.debugPrint("HEY ! " + message);
        ReceivedPacket receivedPacket = new ReceivedPacket(message);
        DNChannel dnChannel = Client.getInstance().getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
            dnChannel.received(receivedPacket);
        }

        if(message.hasRequest()){
            if(message.hasProvider()){
                if(message.getProvider().equals("core")){
                    RequestPacket request = client.getRequestManager().getRequest(Integer.parseInt((String) message.get("RID")));
                    if(request != null)
                        request.getRequestFutureResponse().onReceived(receivedPacket);
                }
            }
            switch (message.getRequest()){
                case CORE_START_SERVER:
                    JVMExecutor startExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(message.getString("SERVERNAME"), JVMContainer.JVMType.SERVER);
                    if(startExecutor == null){
                        return;
                    }
                    startExecutor.startServer();
                    break;
                case CORE_STOP_SERVER:
                    String[] stopServerSplitted = message.getString("SERVERNAME").split("-");
                    JVMExecutor stopExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(stopServerSplitted[0], JVMContainer.JVMType.SERVER);
                    if(stopExecutor == null){
                        return;
                    }
                    stopExecutor.getService(Integer.valueOf(stopServerSplitted[1])).stop();
                    break;
                case SPIGOT_EXECUTE_COMMAND:
                    System.out.println("EXECUTE COMMAND");
                    ClientManager.Client cmdClient = Client.getInstance().getClientManager().getClient(message.getString("SERVERNAME"));
                    if(cmdClient != null){
                        cmdClient.getRequestManager().sendRequest(RequestType.SPIGOT_EXECUTE_COMMAND,message.getString("CMD"));
                    }
                case CORE_RETRANSMISSION:
                    String server = message.getString("RETRANS");
                    
                    Client.getInstance().getClientManager().getClient(server).getChannelHandlerContext().writeAndFlush(message);
                    break;
                case DEV_TOOLS_VIEW_CONSOLE_MESSAGE:
                    ScreenManager.instance.getScreens().get(message.getString("SERVERNAME")).getDevToolsReading().add(client);
                    break;
                case DEV_TOOLS_SEND_COMMAND:
                    boolean b = Boolean.parseBoolean(message.getString("TYPE"));
                    String[] serv = message.getString("SERVERNAME").split("-");
                    String cmd = message.getString("CMD");

                    if(b){
                        JVMExecutor j = Client.getInstance().getJvmContainer().jvmExecutorsProxy.get(serv[0]);
                        if(j == null)
                            return;
                        JVMService jvmService = j.getService(Integer.valueOf(serv[1]));
                        if(jvmService.getClient() != null){
                            jvmService.getClient().getRequestManager().sendRequest(RequestType.BUNGEECORD_EXECUTE_COMMAND,cmd);
                        }
                    }else {
                        JVMExecutor j = Client.getInstance().getJvmContainer().jvmExecutorsServers.get(serv[0]);
                        if(j == null)
                            return;
                        JVMService jvmService = j.getService(Integer.valueOf(serv[1]));
                        if(jvmService.getClient() != null){
                            jvmService.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_EXECUTE_COMMAND,cmd);
                        }
                    }

            }
        }
    }
    
    
}
