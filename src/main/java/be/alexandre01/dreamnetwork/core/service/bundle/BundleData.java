package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class BundleData {
    private final JVMContainer.JVMType jvmType;
    private String name;
    private final ArrayList<BService> services;
    private boolean autoStart;

    private final HashMap<String,JVMExecutor> executors = new HashMap<>();
    private final BundleInfo bundleInfo;

    public BundleData(JVMContainer.JVMType jvmType,String name,  ArrayList<BService> services,boolean autoStart, BundleInfo bundleInfo) {
        this.bundleInfo = bundleInfo;
        this.jvmType = jvmType;
        this.name = name;
       // this.autoStart = autoStart;
        this.services = services;
    }
    public BundleData(String name, BundleInfo bundleInfo) {
        this.bundleInfo = bundleInfo;
        this.jvmType = bundleInfo.getType();
        this.name = name;
        //this.autoStart = autoStart;
        this.services = new ArrayList<>();
    }
}
