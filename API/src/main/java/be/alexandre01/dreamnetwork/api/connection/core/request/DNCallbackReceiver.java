package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import lombok.Getter;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/

@Getter
public class DNCallbackReceiver {
    int MID;
    Message message;
    int timeOut = 20;
    long creationTimeStamp;

    public DNCallbackReceiver(int MID, Message message) {
        this.MID = MID;
        this.message = message;
        if(message.containsKeyInRoot("tOut")){
            timeOut = (int) message.getInRoot("tOut");
        }
        creationTimeStamp = System.currentTimeMillis()-100;
    }

    public boolean isOutOfTime(){
        return (creationTimeStamp+(timeOut*1000L)) <= System.currentTimeMillis();
    }

    public boolean send(String custom){
        //response ID = RID
        // Message ID = MID
        return mergeAndSend(new Message(),custom).isPresent();
    }


    public void send(TaskHandler.TaskType taskType){
        this.send(taskType.toString());
    }

    public Optional<Message> mergeAndSend(Message message, String custom){
        System.out.println("Sending callback");
        if(isOutOfTime()) return Optional.empty();
        System.out.println(this.message.getClientProvider().isPresent());
        if(this.message.getClientProvider().isPresent()){
            UniversalConnection client = this.message.getClientProvider().get();
            message.setInRoot("RID",MID);
            message.setInRoot("tType",custom);
            client.writeAndFlush(message);
            return Optional.of(message);
        }else {
           return Optional.empty();
        }
    }

    public Optional<Message> mergeAndSend(Message message, TaskHandler.TaskType taskType){
        return mergeAndSend(message,taskType.toString());
    }

}
