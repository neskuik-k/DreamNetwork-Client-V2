package be.alexandre01.dreamnetwork.client.service.bundle;

import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaIndex;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaVersion;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BundleManager {
    @Getter
    File javaIndexFile = new File(System.getProperty("user.dir")+"/data/ServiceBundles.json");
    @Getter
    JavaIndex javaIndex = null;

    public BundleManager() {
        if(!javaIndexFile.exists()){
            try {
                javaIndexFile.createNewFile();
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
                javaIndex.put("defaultProxy", new BundleData(JVMContainer.JVMType.PROXY,"defaultProxy"));
                javaIndex.put("defaultServer", new BundleData(JVMContainer.JVMType.SERVER,"defaultServer"));
                javaIndex.refreshFile();
            }
            ArrayList<String> ss = new ArrayList<>(javaIndex.keySet());
            for (String s : ss) {
                JavaVersion javaVersion = javaIndex.getJMap().get(s);
                if(javaVersion.getPath().contains("/") || javaVersion.getPath().contains("\\\\")){
                    File file = new File(Config.getPath(javaVersion.getPath()));
                    if(!file.exists()){
                        javaIndex.remove(javaVersion.getName());
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
