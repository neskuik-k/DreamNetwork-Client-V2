package be.alexandre01.dreamnetwork.utils.spiget;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Spiget {
    public File download(String name,String url){
        try {
            File file = new File(name);
            FileUtils.copyURLToFile(new URL(url),file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public File download(Ressource ressource){
        return null;
    }

    //TEST
    public static void main(String[] args){
        Spiget spiget = new Spiget();

        spiget.download("DiscoArmor.jar","https://www.spigotmc.org/resources/disco-armor-by-packets-sending-fake-armour.27569/download?version=247057");
    }
}
