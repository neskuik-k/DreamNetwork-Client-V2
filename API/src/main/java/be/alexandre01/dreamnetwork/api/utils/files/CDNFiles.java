package be.alexandre01.dreamnetwork.api.utils.files;

import be.alexandre01.dreamnetwork.api.addons.AddonDownloaderObject;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CDNFiles extends Thread{
    private WebFileReader wfr;
    @Getter private boolean instanced = false;

    @Getter private HashMap<String, AddonDownloaderObject> addons = null;
    @Getter private List<String> addonsToUpdate = null;

    @Override
    public synchronized void start() {
        super.start();
        wfr = new WebFileReader();
        readAddons();
        instanced = true;
    }

    private synchronized void readAddons(){
        JsonObject addonsJSON = wfr.readJSONCDN("addon/officials.json");
        if(addonsJSON == null){this.addons = new HashMap<>();return;}
        addonsToUpdate = new ArrayList<>();
        HashMap<String, AddonDownloaderObject> addons = new HashMap<>();
        for(String name : addonsJSON.keySet()){
            JsonObject addonInfo = addonsJSON.getAsJsonObject(name);
            String author = addonInfo.get("author").getAsString();
            String desc = addonInfo.get("description").getAsString();
            System.out.println(addonInfo.get("description"));
            String version = addonInfo.get("version").getAsString();
            String github = addonInfo.get("github").getAsString();
            String downloadLink = addonInfo.get("download").getAsString();
            String hash = addonInfo.get("hash").getAsString();
            addons.put(name, new AddonDownloaderObject(name, author, desc, version, github, downloadLink, hash));

            File addonDownloaded = new File("addons/" + name + ".jar");
            if(addonDownloaded.exists()){
                try {
                    byte[] data = Files.readAllBytes(addonDownloaded.toPath());
                    byte[] fileHash = MessageDigest.getInstance("MD5").digest(data);
                    String checksum = new BigInteger(1, fileHash).toString(16);
                    if(!checksum.equals(hash)){addonsToUpdate.add(name);}
                }catch (IOException | NoSuchAlgorithmException ignored){}
            }
        }
        this.addons = addons;
        /*if(addonsToUpdate.size() > 0){
            addonsToUpdate.forEach(name -> {
                Console.printLang("addons.canUpdate", name, name);
            });
        }*/
    }

    /*public static void getVersions(){
        String versionsJSONString = read("versions/versions.json");
        if(versionsJSONString.equals("INVALID FILE")){

        }
        JsonObject element = new JsonParser().parse(versionsJSONString).getAsJsonObject();

    }*/
}
