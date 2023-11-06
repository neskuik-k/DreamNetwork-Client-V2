package be.alexandre01.dreamnetwork.api.connection.core.communication;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 14:04
*/
public abstract class IResponsesCollection {
    final HashMap<String, CoreReceiver> responses = new HashMap<>();


    public void addResponse(String response, CoreReceiver coreReceiver){
        responses.put(response, coreReceiver);
    }
    public CoreReceiver getResponses(String response){
        return responses.get(response);
    }
}
