package be.alexandre01.dreamnetwork.client.service.jdk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Getter;

public class JavaReader {

    @Getter File javaIndexFile = new File(System.getProperty("user.dir")+"/data/JDKIndex.json");
    @Getter JavaIndex javaIndex = null;

    public JavaReader() {
        if(!javaIndexFile.exists()){
            try {
                javaIndexFile.createNewFile();
                System.out.println(javaIndexFile.getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(javaIndexFile.getAbsolutePath()));
            this.javaIndex = gson.fromJson(reader,JavaIndex.class);
            if(javaIndex == null)
                this.javaIndex = new JavaIndex();
            javaIndex.setJavaIndexFile(javaIndexFile);

            if(javaIndex.isEmpty()){
                javaIndex.put("default", "java");
                javaIndex.reloadFile();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
