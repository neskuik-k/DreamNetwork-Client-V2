package be.alexandre01.dreamnetwork.client.connection.request;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import lombok.Data;


import java.util.HashMap;

@Data
public class RequestBuilder {
    protected HashMap<RequestType,RequestData> requestData;

    protected RequestBuilder(){
        requestData = new HashMap<>();
    }

    public void addRequestBuilder(RequestBuilder... requestBuilders){
        for(RequestBuilder requestBuilder : requestBuilders){
            requestData.putAll(requestBuilder.requestData);
        }
    }


    public interface RequestData {
        public Message write(Message message, ClientManager.Client client, Object... args);
    }

}
