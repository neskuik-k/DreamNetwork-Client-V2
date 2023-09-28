package be.alexandre01.dreamnetwork.api.utils.files;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class WebFileReader{
    public JsonObject readJSONCDN(String file){
        return readJSON("https://cdn.dreamnetwork.cloud/hypervisor/" + file);
    }

    public JsonObject readJSON(String fileURL){
        try {
            URL url = new URL(fileURL);
            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {sb.append(sc.nextLine());}
            return new JsonParser().parse(sb.toString()).getAsJsonObject();
        } catch (IOException e) {
            return null;
        }
    }
}
