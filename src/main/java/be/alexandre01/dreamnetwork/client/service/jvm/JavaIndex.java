package be.alexandre01.dreamnetwork.client.service.jvm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.utils.json.JSONFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import be.alexandre01.dreamnetwork.utils.Tuple;
import lombok.Data;

@Data
public class JavaIndex extends JSONFileUtils {
    private JavaVersion defaultJava = null;
    private HashMap<String, JavaVersion> jMap = new HashMap<>();
    private HashMap<Integer, JavaVersion> jVersion = new HashMap<>();


     @Override
     public Object put(String key,Object value){
         Object k = super.put(key,value);

         if(value instanceof String){
             String t = (String) value;


         JavaVersion javaVersion = new JavaVersion();
         javaVersion.setName(key);
         javaVersion.setPath(t);
         if(key.equals("default")){
             defaultJava = javaVersion;
         }
         getJMap().put(key, javaVersion);
         if(javaVersion.getVersion() != -1){
            jVersion.put(javaVersion.getVersion(),javaVersion);
         }
        }
         return k;
     }
     @Override
     public Object remove(Object key){
        Object k = super.remove(key);

        jMap.remove(key);

        return k;
     }
}    

