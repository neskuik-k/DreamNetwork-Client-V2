package be.alexandre01.dreamnetwork.core.connection.core.communication.services;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.datas.DefaultRemoteData;
import be.alexandre01.dreamnetwork.api.connection.core.datas.RemoteData;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.datas.DataLocalObjects;
import io.netty.channel.ChannelHandlerContext;

import javax.xml.crypto.Data;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 19:04
*/
public class UniversalDataReceiver extends CoreReceiver {
    Core core = Core.getInstance();
    DataLocalObjects objects = core.getDataLocalObjects();
    public UniversalDataReceiver(){
        addRequestInterceptor(RequestType.UNIVERSAL_CALL_DATA,(message, ctx,connection) -> {
            message.getCallback().ifPresent(receiver -> {
                String key;
                if(objects.getLocalDatas().containsKey(key = message.getString("key"))){
                    receiver.mergeAndSend(new Message().set("data",objects.getLocalDatas().get(key)),TaskHandler.TaskType.ACCEPTED);
                }else{
                    receiver.send(TaskHandler.TaskType.FAILED);
                }
            });
        });
        addRequestInterceptor(RequestType.UNIVERSAL_OVERWRITE_DATA,(message, ctx,connection) -> {
            message.getCallback().ifPresent(receiver -> {
                try {
                    objects.setLocalData(message.getString("key"),message.get("data"));
                    receiver.send(TaskHandler.TaskType.ACCEPTED);
                }catch (RuntimeException e){
                    receiver.send(TaskHandler.TaskType.FAILED);
                }
            });
        });

        addRequestInterceptor(RequestType.UNIVERSAL_SUBSCRIBE_DATA,(message, ctx,connection) -> {
                message.getCallback().ifPresent(receiver -> {
                    String key = message.getString("key");
                    if(objects.getDataSubscribers().containsKey(key)){
                        if(objects.getDataSubscribers().get(key).contains(connection)){
                            receiver.send(TaskHandler.TaskType.IGNORED);
                            return;
                        }
                    }
                    objects.getDataSubscribers().put(key,connection);
                    receiver.send(TaskHandler.TaskType.ACCEPTED);
                });
        });

        addRequestInterceptor(RequestType.UNIVERSAL_UNSUBSCRIBE_DATA,(message, ctx,connection) -> {
                message.getCallback().ifPresent(receiver -> {
                    String key = message.getString("key");
                    if(objects.getDataSubscribers().containsKey(key)){
                        if(objects.getDataSubscribers().get(key).contains(connection)){
                            objects.getDataSubscribers().get(key).remove(connection);
                            receiver.send(TaskHandler.TaskType.ACCEPTED);
                            return;
                        }
                    }
                    receiver.send(TaskHandler.TaskType.IGNORED);
                });
        });


        // quand le netserver reçoit une reponse
        addRequestInterceptor(RequestType.UNIVERSAL_SEND_DATA,(message, ctx,connection) -> {
            String key = message.getString("key");
            Object value = message.get("value");
            if(!connection.getDataManager().getDatas().containsKey(key)){
                connection.getDataManager().getDatas().put(key,new DefaultRemoteData<>(key,connection));
            }else {
                RemoteData<?> remoteData = connection.getDataManager().getDatas().get(key);
                if(remoteData instanceof DefaultRemoteData){
                    ((DefaultRemoteData<?>) remoteData).setData(value);
                }
            }
        });
    }
}
