package be.alexandre01.dreamnetwork.core.config;

import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class GlobalSettings extends YamlFileUtils<GlobalSettings> {
    int port;
    String language;
    String folder = "/home/alexandre01/serveur/";

    public GlobalSettings() {
        System.out.println("Loading global settings");
        this.port = 14520;
        this.language = "fr_FR";
    }

    public void loading(){
        addAnnotation("This is the global settings of the server");
        if(!super.config(new File(Config.getPath("data/global.yml")),GlobalSettings.class,true)){
            super.saveFile(GlobalSettings.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }
    }

    public void save(){
        super.saveFile(GlobalSettings.class.cast(this));
    }
}
