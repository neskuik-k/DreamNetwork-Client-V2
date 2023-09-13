package be.alexandre01.dreamnetwork.api.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallbackReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 10/09/2023 at 22:54
*/
public interface ICallbackManager {


    Optional<TaskHandler> getHandlerOf(int MID);

    Optional<DNCallbackReceiver> getReceived(int MID);
}
