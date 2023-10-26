package be.alexandre01.dreamnetwork.core.service.deployment;


import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
public class DeployContainer {

    transient String name;

    DeployData deployData;

    boolean isAvailable;

    File directory;

    long lastSize;

    boolean isNeedUpdate = false;

    StaticUpdater staticUpdater = new StaticUpdater();
    private List<File> linkedDirectories = new ArrayList<>();

    public DeployContainer(File directory) {
        this.directory = directory;
        this.name = directory.getName();
    }


    public boolean load() {
        if(!directory.exists()){
            return false;
        }
        File file = new File(this.directory, "deploy.yml");
        if(!file.exists()){
            return false;
        }

        DeployData.loading(file).ifPresent(deployData -> {
            this.deployData = deployData;

            if(this.deployData.getLastSize() == null){
                this.deployData.setLastSize(FileUtils.sizeOfDirectory(this.directory));
                this.deployData.getYamlFileUtils().saveFile();
            }else {
                this.lastSize = this.deployData.getLastSize();
            }
        });

        return deployData != null;
    }

    public CompletableFuture<Boolean> updateStaticFolder(File to){
        if(this.deployData == null){
            return CompletableFuture.completedFuture(false);
        }
        if(!isModified() && !isNeedUpdate()){
            return CompletableFuture.completedFuture(true);
        }
        isNeedUpdate = true;
        if(!isModified()){
            if(!linkedDirectories.contains(to)){

                return staticUpdater.update(directory,to);
            }
            return CompletableFuture.completedFuture(true);
        }
        linkedDirectories.clear();
        this.deployData.setLastSize(FileUtils.sizeOfDirectory(this.directory));
        return staticUpdater.update(directory,to);
    }


    public boolean isModified(){
        return lastSize != FileUtils.sizeOfDirectory(this.directory);
    }

}
