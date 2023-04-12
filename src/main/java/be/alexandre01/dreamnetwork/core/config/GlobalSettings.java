package be.alexandre01.dreamnetwork.core.config;

import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;

import java.io.File;

public class GlobalSettings extends YamlFileUtils<GlobalSettings> {
    @Getter int port = 14520;
    @Getter String language = "fr";

    public GlobalSettings() {
        if(!super.config(new File(Config.getPath("data/global.yml")),GlobalSettings.class,false)){
            super.readFile(this);
        }else {
            super.loadFile(getFile(),GlobalSettings.class);
        }
    }
}
