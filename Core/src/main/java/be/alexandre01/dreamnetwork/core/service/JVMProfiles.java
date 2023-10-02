package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.util.HashMap;

public class JVMProfiles extends YamlFileUtils<ProfilesData> implements IProfiles {
    ProfilesData profilesData = null;


    public JVMProfiles() {
        super(ProfilesData.class);
        addTag(JVMProfiles.class,Tag.MAP);
        addTag(ConfigData.class,Tag.MAP);


        representer = new CustomRepresenter(true,JVMProfiles.class,ConfigData.class);
        representer.addClassTag(JVMProfiles.class, Tag.MAP);
        representer.addClassTag(ConfigData.class, Tag.MAP);

        //addTag(JVMConfig2.class,new Tag("!be.alexandre01.dreamnetwork.core.service.JVMConfig2"));
        constructor = new Constructor(JVMProfiles.class,new LoaderOptions());
        TypeDescription jvmConfigDescription = new TypeDescription(JVMProfiles.class);
        jvmConfigDescription.putMapPropertyType("profiles", String.class, ConfigData.class);
        constructor.addTypeDescription(jvmConfigDescription);
    }
    public void loading(File file){

        addAnnotation("Profiles files");

        init(file,true).ifPresent(profilesData -> {
            this.profilesData = profilesData;
        });



        /*if(!super.config(file, ProfilesData.class,true)){
            profilesData = createObject();
        }else {
            try {
                profilesData = readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            save();
        }*/
       // System.out.println("Loading JVMConfigs "+ profiles.size() + ":"+profiles.toString());

    }

    @Override
    public void save(){
        super.saveFile(profilesData);
    }

    @Override
    public HashMap<String, IConfig> getProfiles() {
        return profilesData.profiles;
    }

    @Override
    public ProfilesData createObject() {
        profilesData = new ProfilesData();
        profilesData.profiles = new HashMap<>();
        JVMConfig config = new JVMConfig();
        config.setName("test");
        config.setXms("1024M");
        config.setXmx("1024M");
        profilesData.profiles.put("hello", config);
        super.saveFile(profilesData);
        return profilesData;
    }
}
