package be.alexandre01.dreamnetwork.client.connection.request;

import be.alexandre01.dreamnetwork.client.utils.messages.Message;


import java.util.HashMap;

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


    public interface RequestData{
        public Message write(Message message, String... args);
    }

}
