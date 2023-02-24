package be.alexandre01.dreamnetwork.api.yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BasicYamlReader {

    public BasicYamlReader(File file, YamlConfig yamlConfig, YamlFile yamlFile) {
        if(!file.exists()){
            try {
                file.createNewFile();

                yamlConfig.getDefaults().forEach((key, value) -> {
                    yamlFile.getWriter().addCache(key+": "+value);
                });
                yamlFile.getWriter().saving();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(file));
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.contains(":")){
                    String[] split = line.split(":");
                    Object o = split[1];
                    yamlConfig.set(split[0],o);
                }else{
                    yamlConfig.addExtraLine(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
