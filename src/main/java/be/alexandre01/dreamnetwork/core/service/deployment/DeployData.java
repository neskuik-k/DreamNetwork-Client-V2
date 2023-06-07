package be.alexandre01.dreamnetwork.core.service.deployment;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;

import java.io.File;

@Getter
public class DeployData extends YamlFileUtils<DeployData> {

    transient String name;

    String author = Main.getGlobalSettings().getUsername();
    String[] deployTypes = new String[]{"CONFIGURATION"};
    String compatibleVersions = "UNKNOWN";
    String version = "1.0";


    public DeployData(){

    }
    public boolean loading(File file){
        addAnnotation("Deployment folder for Services");
        name = file.getName();
        if(!super.config(file, DeployData.class,true)){
            super.saveFile(DeployData.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }
        return true;
    }

    public void save(){
        super.saveFile(DeployData.class.cast(this));
    }

    public DeployType[] getDeployTypes(){
        return new DeployType[0];
    }
    public enum DeployType{
        CONFIGURATIONS,
        ONLY_MAP,
        CUSTOM,
        EXEC_JAR,
        ALL_IN_ONE
    }
}

