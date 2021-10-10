package be.alexandre01.dreamnetwork.client.service.bundle;

import lombok.Data;

@Data
public class BService {
    private String serviceName;
    private int totalCount;

    public BService(String serviceName, int totalCount) {
        this.serviceName = serviceName;
        this.totalCount = totalCount;
    }
}
