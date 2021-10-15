package be.alexandre01.dreamnetwork.client.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

@Data
public class JSONFileUtils extends LinkedHashMap<String, Object> {
    private File indexFile;

    public void refreshFile(){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            FileWriter f = new FileWriter(indexFile);
            gson.toJson(this,f);
            f.flush();
            f.close();
        } catch (JsonIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
