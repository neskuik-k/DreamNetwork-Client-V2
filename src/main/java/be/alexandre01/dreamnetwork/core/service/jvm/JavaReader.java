package be.alexandre01.dreamnetwork.core.service.jvm;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.alexandre01.dreamnetwork.core.config.Config;
import com.google.gson.Gson;

import lombok.Getter;

public class JavaReader {

    @Getter File javaIndexFile = new File(System.getProperty("user.dir")+"/data/JDKIndex.json");
    @Getter JavaIndex javaIndex = null;

    public JavaReader() {
        if(!javaIndexFile.exists()){
            try {
                javaIndexFile.createNewFile();
              //  System.out.println(javaIndexFile.getAbsolutePath());
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
            javaIndex.setIndexFile(javaIndexFile);

            if(javaIndex.isEmpty()){
                javaIndex.put("default", "java");
                if(!Config.isWindows())
                    searchDefaultJavaFolder("/usr/lib/jvm/");
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


    //Search default java folder on linux
    public void searchDefaultJavaFolder(String path){
        File file = new File(Config.getPath(path));
        if(!file.exists()){
            return;
        }

        try (Stream<Path> files = Files.list(Paths.get(file.toURI()))) {
            Collection<Path> paths = files.collect(Collectors.toList());
            for(Path p : paths){
                if(Files.isDirectory(p)){
                    checkFolderJava(path+p.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkFolderJava( String path){
        File file = new File(Config.getPath(path));
        if(!file.exists()){
            return;
        }

        try (Stream<Path> files = Files.list(Paths.get(file.toURI()))) {
            Collection<Path> paths = files.collect(Collectors.toList());
            for(Path p : paths){
                if(Files.isDirectory(p)){
                    if(p.getFileName().toString().equalsIgnoreCase("bin")){
                        String[] s = file.getName().split("-");
                        if(s.length > 1){
                            javaIndex.put(s[1],path+"/bin/java");
                        }else {
                            javaIndex.put(s[0],path+"/bin/java");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
