package be.alexandre01.dreamnetwork.core.connection.external.communication;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


public class ExternalTransmission extends CoreReceiver {
    public ExternalTransmission(){
        super.addRequestInterceptor(RequestType.CORE_HANDSHAKE_STATUS, new RequestInterceptor() {

            @Override
            public void onRequest(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
                if(message.getString("STATUS").equalsIgnoreCase("SUCCESS")){
                    System.out.println("I'm connected to the core YEEPEE");
                    ExternalCore.getInstance().setConnectionID(message.getString("ID"));
                    ExternalCore.getInstance().setConnected(true);

                    ArrayList<ConfigData> list = new ArrayList<>();
                    DNCoreAPI.getInstance().getContainer().getJVMExecutors().forEach(ijvmExecutor -> {
                        if(ijvmExecutor.getConfig() instanceof ConfigData){
                            list.add((ConfigData) ijvmExecutor.getConfig());
                        }
                    });

                    //list.add(new Test());
                    //list.add(new Test());
                    System.out.println(client);

                    client.getRequestManager().sendRequest(RequestType.CORE_REGISTER_EXTERNAL_EXECUTORS , list);

                    /*HashMap<String, ConfigData> map = new HashMap<>();
                    map.put("Server1", (ConfigData) DNCoreAPI.getInstance().getContainer().getJVMExecutors().get(0));

                    ExternalCore.getInstance().writeAndFlush(new Message().setHeader("Test").set("Salut",map, ConfigData.class));*/
                }else {
                    System.out.println("I'm not connected to the core :(");
                    ExternalCore.getInstance().setConnected(false);
                    ExternalCore.getInstance().exitMode();
                }
            }
        });

        addRequestInterceptor(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE,(message, ctx, client) -> {
            String serverName = message.getString("DATA");
            IContainer container = DNCoreAPI.getInstance().getContainer();
            message.getCallback().ifPresent(receiver -> {
                Optional<IService> optionalService = container.tryToGetService(serverName);
                if(optionalService.isPresent()){
                    IService iService = optionalService.get();
                    IScreen screen = iService.getScreen();
                    if(screen.getDevToolsReading().contains(client)){
                        screen.getDevToolsReading().remove(client);
                    }else {
                        screen.getDevToolsReading().add(client);
                    }
                    receiver.send(TaskHandler.TaskType.ACCEPTED);
                }else {
                    receiver.send(TaskHandler.TaskType.REJECTED);
                }
            });
        });
        addRequestInterceptor(RequestType.DEV_TOOLS_SEND_COMMAND,(message, ctx, client) -> {
            System.out.println("Find ");
            String serverName = message.getString("SERVICE");
            String command = message.getString("CMD");
            IContainer container = DNCoreAPI.getInstance().getContainer();
               container.tryToGetService(serverName).ifPresent(iService -> {
                    IScreen screen = iService.getScreen();
                    try {
                        screen.getScreenStream().getScreenOutWriter().writeOnConsole(command);
                    } catch (IOException e) {
                        Console.bug(e);
                    }
                });
        });
    }

    @Override
    public void onReceive(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
        System.out.println(message);
        IDNChannelManager channelManager = DNCoreAPI.getInstance().getChannelManager();
        AChannelPacket receivedPacket = channelManager.createChannelPacket(message);
        if(message.getHeader().equals("channel")) {
        IDNChannel dnChannel =channelManager.getChannel(message.getChannel());
        if(dnChannel != null){
                if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                    for (AChannelPacket.DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                        dnChannelInterceptor.received(receivedPacket);
                    }
                }
                return;
            }
        }
        if(message.hasRequest()){
            RequestInfo requestInfo = message.getRequest();

            if(requestInfo.equals(RequestType.SERVER_HANDSHAKE_SUCCESS)) {
                final ExternalCore externalCore = ExternalCore.getInstance();
                String processName = message.getString("PROCESSNAME");
                // networkBaseAPI.setProcessName("s-" + processName);
              //  networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    //networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: " + ctx.channel().remoteAddress());

               // NetworkBaseAPI.getInstance().callServerAttachedEvent();
            }  else if(requestInfo.equals(RequestType.PROXY_HANDSHAKE_SUCCESS)){
                String processName = message.getString("PROCESSNAME");
             //   final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
                //networkBaseAPI.setProcessName("p-"+processName);
               // networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    //networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
            }
        }
    }
}
