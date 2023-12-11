package be.alexandre01.dreamnetwork.api.service.bundle;

import be.alexandre01.dreamnetwork.api.service.IContainer;


import be.alexandre01.dreamnetwork.api.service.IExecutor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class BundleData {
    private final IContainer.JVMType jvmType;
    private String name;
    private final ArrayList<BService> services;
    private boolean autoStart;
    private boolean isVirtual = false;
    private Optional<String> virtualName = Optional.empty();
    private boolean merge = false;
    private final ConcurrentHashMap<String, IExecutor> executors = new ConcurrentHashMap<>();
    private final IBundleInfo bundleInfo;

    public BundleData(IContainer.JVMType jvmType,String name,  ArrayList<BService> services,boolean autoStart, IBundleInfo bundleInfo) {
        this.bundleInfo = bundleInfo;
        this.jvmType = jvmType;
        this.name = name;
       // this.autoStart = autoStart;
        this.services = services;
    }
    public BundleData(String name, IBundleInfo bundleInfo) {
        this.bundleInfo = bundleInfo;
        this.jvmType = bundleInfo.getType();
        this.name = name;
        //this.autoStart = autoStart;
        this.services = new ArrayList<>();
    }


    public String getPathName(){
        return name+"/";
    }
}
