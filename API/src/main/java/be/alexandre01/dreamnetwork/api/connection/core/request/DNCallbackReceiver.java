package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/

@Getter
public class DNCallbackReceiver {
    int MID;
    Message message;
    boolean proceedLater = false;

    public DNCallbackReceiver(int MID, Message message) {
        this.MID = MID;
        this.message = message;
    }

    public boolean send(String custom){
        //response ID = RID
        // Message ID = MID
        if(mergeIn(new Message(),custom).isPresent()) {
            message.getProvider().ifPresent(client -> {
                client.writeAndFlush(message);
            });
            return true;
        }
        return false;
    }

    public void send(TaskHandler.TaskType taskType){
        this.send(taskType.toString());
    }

    public Optional<Message> mergeIn(Message message, String custom){
        if(message.getProvider().isPresent()){
            IClient client = message.getProvider().get();
            message.setInRoot("RID",MID);
            message.setInRoot("tType",custom);
            client.writeAndFlush(message);
            return Optional.of(message);
        }else {
           return Optional.empty();
        }
    }

    public Optional<Message> mergeIn(Message message, TaskHandler.TaskType taskType){
        return mergeIn(message,taskType.toString());
    }

    public void ignoreForNow(){
        proceedLater = true;
    }
}
