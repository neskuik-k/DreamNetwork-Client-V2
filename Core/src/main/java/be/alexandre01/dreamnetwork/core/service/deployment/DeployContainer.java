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
        File file = new File(this.file, "deploy.yml");
        if(!file.exists()){
            return false;
        }

        DeployData.loading(file).ifPresent(deployData -> {
            this.deployData = deployData;
        });

        return deployData != null;
    }

}
