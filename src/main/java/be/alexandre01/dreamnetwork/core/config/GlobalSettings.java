package be.alexandre01.dreamnetwork.core.config;

import be.alexandre01.dreamnetwork.core.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class GlobalSettings extends YamlFileUtils<GlobalSettings> {
    boolean SIG_IGN_Handler = true;
    boolean findAllocatedPorts = true;
    boolean checkDefaultJVMVersion = true;
    boolean checkJVMVersionOnServiceStart = true;
    boolean rainbowText = false;

    int threadPoolIO= 10;
    String copyIOMethod = "files";

    boolean screenNameInConsoleChange= true;
    String username = null;
    int port = 14520;
    String language = "en_EN";
    private boolean useEmoji = false;
    private boolean emojiOnCommand= false;

    public GlobalSettings() {
        // Init
    }

    public void loading(){
        String[] randomString = {"Better, faster, stronger", "The Dreamy Networky the best !", "If you see this message, you are the best", ":)","<3",":D","Thank you for using our hypervisor","Roblox is better than minecraft (it's joke huh)","Sadness is the opposite of happiness"};

        String random = randomString[(int) (Math.random() * randomString.length)];
        addAnnotation("This is the global settings of the server | " + random);
        if(!super.config(new File(Config.getPath("data/Global.yml")),GlobalSettings.class,true)){
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
