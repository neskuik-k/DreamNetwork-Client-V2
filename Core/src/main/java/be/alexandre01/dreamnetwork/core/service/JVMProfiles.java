package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.util.HashMap;

public class JVMProfiles extends YamlFileUtils<JVMProfiles> implements IProfiles {
    @Getter public HashMap<String,IConfig> profiles;

    public JVMProfiles() {
        addTag(JVMProfiles.class,Tag.MAP);
        addTag(JVMConfig.class,Tag.MAP);


        representer = new CustomRepresenter(true,JVMProfiles.class,JVMConfig.class);
        representer.addClassTag(JVMProfiles.class, Tag.MAP);
        representer.addClassTag(JVMConfig.class, Tag.MAP);

        //addTag(JVMConfig2.class,new Tag("!be.alexandre01.dreamnetwork.core.service.JVMConfig2"));
        constructor = new Constructor(JVMProfiles.class);
        TypeDescription jvmConfigDescription = new TypeDescription(JVMProfiles.class);
        jvmConfigDescription.putMapPropertyType("profiles", String.class, JVMConfig.class);
        constructor.addTypeDescription(jvmConfigDescription);
    }
    public void loading(File file){
        profiles = null;
        addAnnotation("Profiles files");

        if(!super.config(file, JVMProfiles.class,true)){
            profiles = new HashMap<>();
            JVMConfig config = new JVMConfig();
            config.name = "test";
            config.setXms("1024M");
            config.setXmx("1024M");
            profiles.put("hello", config);
            super.saveFile(JVMProfiles.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }
       // System.out.println("Loading JVMConfigs "+ profiles.size() + ":"+profiles.toString());

    }

    @Override
    public void save(){
        super.saveFile(JVMProfiles.class.cast(this));
    }

}
