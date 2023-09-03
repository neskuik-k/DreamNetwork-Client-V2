package be.alexandre01.dreamnetwork.api.service.bundle;

import lombok.Data;

@Data
public class BService {
    private String serviceName;
    private int totalCount;
    private Integer activeCount;
    private IBundleInfo bundleInfo;


    public BService(String serviceName, int totalCount, Integer activeCount) {
        this.serviceName = serviceName;
        this.totalCount = totalCount;
        this.activeCount = activeCount;
    }

    public BService(){

    }
}
