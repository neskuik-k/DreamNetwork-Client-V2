package be.alexandre01.dreamnetwork.core.service.deployment;


import lombok.Data;

import java.io.File;

@Data
public class DeployContainer {

    transient String name;

    DeployData deployData;

    boolean isAvailable;

    File file;

    public DeployContainer(File file) {
        this.file = file;
        this.name = file.getName();
    }


    public boolean load() {
        if(!file.exists()){
            return false;
        }
        this.deployData = new DeployData();
        File file = new File(this.file, "deploy.yml");
        if(!file.exists()){
            return false;
        }
        boolean b = deployData.loading(file);
        return b;
    }

}
