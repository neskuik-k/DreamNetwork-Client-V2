package be.alexandre01.dreamnetwork.api.service.deployment;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.SkipInitCheck;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

@Getter @Setter
public class DeployData implements Deploy{

    @Ignore private String name;
    @Ignore private File directory;
    @Ignore private YamlFileUtils<DeployData> yamlFileUtils;

    private String author = DNUtils.get().getConfigManager().getGlobalSettings().getUsername();
    private String[] types =  new String[]{"CONFIGURATION"};
    private String compatibleVersions = "UNKNOWN";
    private String version = "1.0";
    @SkipInitCheck private Long lastSize = null;




    public DeployData(){
        // Init

    }

    public static Optional<DeployData> loading(File file){
        YamlFileUtils<DeployData> yml = new YamlFileUtils<>(DeployData.class);
        yml.addAnnotation("Deployment folder for Services");

        Optional<DeployData> d = yml.init(file,true);
        d.ifPresent(deployData -> {
            deployData.setDirectory(file.getParentFile());
            deployData.setName(deployData.getDirectory().getName());
            deployData.setYamlFileUtils(yml);
        });
        return d;

    }

    
  /*if(!super.config(file, DeployData.class,true)){
            super.saveFile(DeployData.class.cast(this));
        }else {
            super.readAndReplace(this);
            save();
        }*/


 /*   public void save(){
        super.saveFile(DeployData.class.cast(this));
    }*/

    public DeployType[] getDeployTypes(){
        return Arrays.stream(types).map(DeployType::valueOf).toArray(DeployType[]::new);
    }



    public enum DeployType{
        CONFIGURATIONS,
        ONLY_MAP,
        CUSTOM,
        EXEC_JAR,
        ALL_IN_ONE
    }
}

