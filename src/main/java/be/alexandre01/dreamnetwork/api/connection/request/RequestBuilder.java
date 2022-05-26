package be.alexandre01.dreamnetwork.api.connection.request;

import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import lombok.Data;


import java.util.HashMap;

@Data
public class RequestBuilder {
    protected HashMap<RequestType, RequestData> requestData;

    public RequestBuilder(){
        requestData = new HashMap<>();
    }

    public void addRequestBuilder(RequestBuilder... requestBuilders){
        for(RequestBuilder requestBuilder : requestBuilders){
            requestData.putAll(requestBuilder.requestData);
        }
    }

    public interface RequestData {
        /**
         *
         * @param message
         * @param client
         * @param args
         * @return the {@linkplain Message message}
         */
        public Message write(Message message, Client client, Object... args);


    }
}
