package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallbackReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;

import java.util.HashMap;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 09/09/2023 at 11:32
*/
public class CallbackManager implements ICallbackManager {
    HashMap<Integer, TaskHandler> sendedCallbacksHashMap = new HashMap<>();
    public CallbackManager() {

    }
    public void addCallback(int MID, TaskHandler handler) {
        sendedCallbacksHashMap.put(MID, handler);
        TaskHandler.getTimeStamps().put(handler,System.currentTimeMillis()+(handler.getTimeOut()*1000L));
    }

    @Override
    public void removeCallback(int MID,TaskHandler handler) {
        TaskHandler.getTimeStamps().remove(handler);
        sendedCallbacksHashMap.remove(MID);
    }


    public void addCallback(int MID, DNCallback callback) {
        this.addCallback(MID, callback.getHandler());
    }
    @Override
    public Optional<TaskHandler> getHandlerOf(int MID){
        return Optional.ofNullable(sendedCallbacksHashMap.get(MID));
    }
}
