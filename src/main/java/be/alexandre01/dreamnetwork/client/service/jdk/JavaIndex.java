package be.alexandre01.dreamnetwork.client.service.jdk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.utils.Tuple;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class JavaIndex extends LinkedHashMap<String, Object> {
    private JavaVersion defaultJava = null;
    private HashMap<String, JavaVersion> jMap = new HashMap<>();
    File javaIndexFile;

    public JavaIndex() {
        for(String string : super.keySet()){
            Object o = super.get(string);
            if(o instanceof Tuple){
                Tuple<String,String> t = (Tuple<String, String>) super.get(string);


                JavaVersion javaVersion = new JavaVersion();
                javaVersion.setVersion(t.a());
                javaVersion.setPath(t.b());
                if(string.equals("default")){
                    defaultJava = javaVersion;
                }
                
                jMap.put(string, javaVersion);
            }
        }
    
     }

     public void reloadFile(){
        Gson gson = new GsonBuilder().create();
         try {
            FileWriter f = new FileWriter(javaIndexFile);
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

