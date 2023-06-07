package be.alexandre01.dreamnetwork.api.connection.request;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.Getter;

@Data
public class RequestBuilder {
    @Getter private static final Multimap<RequestInfo, RequestData> globalRequestData = ArrayListMultimap.create();
    protected Multimap<RequestInfo, RequestData> requestData;

    public RequestBuilder(){
        requestData = ArrayListMultimap.create();
    }

    public void addRequestBuilder(RequestBuilder... requestBuilders){
        for(RequestBuilder requestBuilder : requestBuilders){
            requestData.putAll(requestBuilder.requestData);
        }
    }

    public static void addGlobalRequestData(RequestBuilder... requestBuilders){
        for(RequestBuilder requestBuilder : requestBuilders){
            globalRequestData.putAll(requestBuilder.requestData);
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
        public Message write(Message message, IClient client, Object... args);

    }
}
