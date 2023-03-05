package be.alexandre01.dreamnetwork.core.service.bundle;

import lombok.Data;

@Data
public class BService {
    private String serviceName;
    private int totalCount;
    private Integer activeCount;
    private BundleInfo bundleInfo;


    public BService(String serviceName, int totalCount, Integer activeCount) {
        this.serviceName = serviceName;
        this.totalCount = totalCount;
        this.activeCount = activeCount;
    }

    public BService(){

    }
}
