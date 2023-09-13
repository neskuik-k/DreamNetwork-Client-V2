package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.service.tasks.ATaskData;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import lombok.Getter;
import lombok.Setter;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/
@Getter
public abstract class TaskHandler {


    public TaskType taskType;
    public Message response;



    public boolean hasType(TaskType taskType){
        return this.taskType == taskType;
    }
    public String getCustomType(){
        return (String) response.getInRoot("tType");
    }

    public void onAccepted(){}

    public void onRefused(){}

    public void onIgnored(){}

    public void onFailed(){}
    public void onCallback(){}

    public void onTimeout(){}

    public enum TaskType{
        IGNORED,
        REFUSED,
        ACCEPTED,
        SUCCESS,
        FAILED,
        CUSTOM,
        TIMEOUT;
    }


    public void setupHandler(Message message){
        this.response = message;
        this.taskType = TaskType.valueOf(message.getInRoot("tType").toString());
    }
}
