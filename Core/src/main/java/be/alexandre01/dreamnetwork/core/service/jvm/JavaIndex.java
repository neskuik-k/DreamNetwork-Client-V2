package be.alexandre01.dreamnetwork.core.service.jvm;

import java.util.HashMap;

import be.alexandre01.dreamnetwork.api.utils.files.json.JSONFileUtils;

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

