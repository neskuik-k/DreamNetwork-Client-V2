package be.alexandre01.dreamnetwork.api.connection.request;

import lombok.Getter;
import lombok.Setter;

public class CustomRequestInfo extends RequestInfo {
    @Getter @Setter
    String customName;

    public CustomRequestInfo(String name){
        super(RequestType.getFreeID(),name);
        RequestType.allIds.add(id());
    }
}
