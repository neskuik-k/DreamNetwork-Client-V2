package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IResponsesCollection;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.connection.core.communication.services.BaseResponse;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 14:09
*/
public class ResponsesCollection extends IResponsesCollection {
    public ResponsesCollection(){
        addResponse(new BaseResponse());
    }
}
