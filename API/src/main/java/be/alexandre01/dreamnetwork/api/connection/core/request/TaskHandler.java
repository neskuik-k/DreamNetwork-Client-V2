package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/
@Getter
public abstract class TaskHandler {


    public TaskType taskType;
    public Message response;
    public int MID;
    public boolean isSingle;

    private int timeOut = 10;

    public TaskHandler(int timeOutInSeconds){
        this.timeOut = timeOutInSeconds;
    }

    public TaskHandler(){}

    public void setTimeOut(int seconds){
        this.timeOut = seconds;
    }

    @Getter static final HashMap<TaskHandler,Long> timeStamps = new HashMap<>();



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

    public void destroy() {
        DNCoreAPI.getInstance().getCoreHandler().getCallbackManager().removeCallback(MID,this);
        DNCallback.getCurrentId().remove(MID);
    }
    public void setupHandler(Message message){
        this.response = message;
        try {
            this.taskType = TaskType.valueOf(message.getInRoot("tType").toString());
            this.MID = (int) message.getInRoot("RID");
        }catch (Exception e){
            this.taskType = TaskType.CUSTOM;
        }
    }
    static {
        Executors.newScheduledThreadPool(2).scheduleAtFixedRate(() -> {
            Long l = System.currentTimeMillis();
            System.out.println("Executor check");

            //non blocking timeStamps loop
            List<TaskHandler> handlersToRemove = new ArrayList<>();

            timeStamps.forEach((taskHandler, timestamp) -> {
                if(timestamp-l <= 0){
                    taskHandler.onTimeout();
                    taskHandler.onFailed();
                    handlersToRemove.add(taskHandler);
                }
            });

            handlersToRemove.forEach(handler -> {
                handler.destroy();
                timeStamps.remove(handler);
            });
        }, 5, 5, TimeUnit.SECONDS);
    }
}
