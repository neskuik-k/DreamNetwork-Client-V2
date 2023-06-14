package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.api.service.IStartupConfigBuilder;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.edit.JVM;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.config.GlobalSettings;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JVMProfiles extends YamlFileUtils<JVMProfiles> {
    @Getter public HashMap<String,JVMConfig> profiles;

    public JVMProfiles() {
        addTag(JVMProfiles.class,Tag.MAP);
        addTag(JVMConfig.class,Tag.MAP);


        representer = new CustomRepresenter(false,JVMProfiles.class,JVMConfig.class);
        representer.addClassTag(JVMProfiles.class, Tag.MAP);
        representer.addClassTag(JVMConfig.class, Tag.MAP);

        //addTag(JVMConfig2.class,new Tag("!be.alexandre01.dreamnetwork.core.service.JVMConfig2"));
        constructor = new Constructor(JVMProfiles.class);
        TypeDescription jvmConfigDescription = new TypeDescription(JVMProfiles.class);
        jvmConfigDescription.putMapPropertyType("profiles", String.class, JVMConfig.class);
        constructor.addTypeDescription(jvmConfigDescription);
    }
    public void loading(File file){
        addAnnotation("Profiles files");
        System.out.println(file.exists());

        if(!super.config(file, JVMProfiles.class,true)){
            profiles = new HashMap<>();
            JVMConfig config = new JVMConfig();
            config.name = "hello";
            config.xms = "1024M";
            config.xmx = "1024M";
            profiles.put("hello", config);
            super.saveFile(JVMProfiles.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }
        System.out.println("Loading JVMConfigs "+ profiles.size() + ":"+profiles.toString());

        for (Map.Entry<String, JVMConfig> profile : profiles.entrySet()) {
            System.out.println("Profile >" +profile.getKey());
            System.out.println("Profile >" +profile.getValue());
        }
    }

    public void save(){
        super.saveFile(JVMProfiles.class.cast(this));
    }

}
