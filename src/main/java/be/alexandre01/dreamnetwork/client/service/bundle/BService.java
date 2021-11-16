package be.alexandre01.dreamnetwork.client.service.bundle;

import be.alexandre01.dreamnetwork.client.events.EventCatcher;
import lombok.Data;

@Data
public class BService {
    private String serviceName;
    private int totalCount;
    private Integer activeCount;

    public BService(String serviceName, int totalCount,Integer activeCount) {
        this.serviceName = serviceName;
        this.totalCount = totalCount;
        this.activeCount = activeCount;
    }
}
