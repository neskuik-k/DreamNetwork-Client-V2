package be.alexandre01.dreamnetwork.api.config;

import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.SkipInitCheck;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter @Setter
public class GlobalSettings {
    boolean SIG_IGN_Handler = true;
    boolean findAllocatedPorts = true;
    boolean checkDefaultJVMVersion = true;
    boolean checkJVMVersionOnServiceStart = true;
    boolean rainbowText = false;

    String[] authorizedIPS = new String[]{"127.0.0.1"};
    @Ignore
    List<String> authorizedIPList;

    boolean randomizePort = false;
    String portRange = "25565 -> 51130"; // max 65535

    @Ignore int[] portRangeInt = new int[2];

    String terminalMode = "ssh";

    int threadsPoolIO= 8;
    String copyIOMethod = "files";

    boolean screenNameInConsoleChange= true;
    @SkipInitCheck String username = null;
    int port = 14520;
    String language = "en_EN";
    private boolean useEmoji = false;
    private boolean emojiOnCommand= false;
    private boolean simplifiedNamingService = true;

    private int nettyWorkerThreads = 4;
    private int nettyBossThreads = 1;
    private String connectionMode = "netty";

    private boolean loggingService = true;
    private int logsByExecutor = 15;

    private boolean externalScreenViewing = true;
    private int historySize = 3250;

    @Ignore private TerminalMode termMode;

    @Getter static YamlFileUtils<GlobalSettings> yml;



    public GlobalSettings() {

        // Init
    }

    public static Optional<GlobalSettings> load(){
        yml = new YamlFileUtils<>(GlobalSettings.class);
        String[] randomString = {"Better, faster, stronger", "The Dreamy Networky the best !", "If you see this message, you are the best", ":)","<3",":D","Thank you for using our hypervisor","Roblox is better than minecraft (it's joke huh)","Sadness is the opposite of happiness"};

        String random = randomString[(int) (Math.random() * randomString.length)];
        yml.addAnnotation("This is the global settings of the server | " + random);

        return yml.init(new File(Config.getPath("data/Global.yml")),false);
    }

    public void loading(){
        termMode = TerminalMode.valueOf(terminalMode.toUpperCase());

        String[] portRange = this.portRange.split(" -> ");
        String portRange1 = portRange[0];
        portRange1 = portRange1.replace(" ","");
        if(portRange1.matches("[0-9]+"))
            portRangeInt[0] = Integer.parseInt(portRange1);
        String portRange2 = portRange[1];
        portRange2 = portRange2.replace(" ","");
        if(portRange2.matches("[0-9]+"))
            portRangeInt[1] = Integer.parseInt(portRange2);

        if(authorizedIPS == null)
            authorizedIPS = new String[]{"127.0.0.1"};

        authorizedIPList = new ArrayList<>(Arrays.asList(authorizedIPS));
    }




    public enum TerminalMode{
        SSH,SAFE;
    }
}
