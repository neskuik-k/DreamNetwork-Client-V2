package be.alexandre01.dreamnetwork.core.service.deployment;

import lombok.Getter;

import java.io.File;
import java.util.HashMap;

public class DeployManager {

    @Getter private final HashMap<String,DeployContainer> deployDataHashMap = new HashMap<>();

    public void addDeploy(DeployContainer deployContainer){
        deployDataHashMap.put(deployContainer.getName(),deployContainer);
        System.out.println("Added deploy: "+deployContainer.getName());
    }

    public void removeDeploy(String name){
        deployDataHashMap.remove(name);
    }

    public DeployContainer getDeploy(String name){
        return deployDataHashMap.get(name);
    }
}
