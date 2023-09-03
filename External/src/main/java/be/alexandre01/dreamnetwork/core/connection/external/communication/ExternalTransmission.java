package be.alexandre01.dreamnetwork.core.connection.external.communication;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.external.ExecutorData;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.connection.external.requests.ExtResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;


public class ExternalTransmission extends ExtResponse {
    public ExternalTransmission(){
        super.addRequestInterceptor(RequestType.CORE_HANDSHAKE_STATUS, new RequestInterceptor() {
            @Override
            public void onRequest(Message message, ChannelHandlerContext ctx) throws Exception {
                if(message.getString("STATUS").equalsIgnoreCase("SUCCESS")){
                    System.out.println("I'm connected to the core YEEPEE");
                    ExternalCore.getInstance().setConnected(true);

                    ArrayList<ExecutorData> list = new ArrayList<>();
                    list.add(new ExecutorData());
                    list.add(new ExecutorData());

                    ExternalCore.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_EXTERNAL_EXECUTORS , list);

                }else {
                    System.out.println("I'm not connected to the core :(");
                    ExternalCore.getInstance().setConnected(false);
                    ExternalCore.getInstance().exitMode();
                }
            }
        });
    }

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
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
