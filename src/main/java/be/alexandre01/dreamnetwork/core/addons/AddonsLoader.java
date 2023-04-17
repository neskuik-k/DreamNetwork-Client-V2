package be.alexandre01.dreamnetwork.core.addons;

import be.alexandre01.dreamnetwork.api.addons.Addon;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import lombok.Getter;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AddonsLoader {
    private File dir;
    @Getter private final ArrayList<Addon> addons = new ArrayList<>();

    @Getter private final ArrayList<Addon> cachedAddons = new ArrayList<>();

    File cacheFolder;
    Yaml yaml = new Yaml();


    public AddonsLoader() {

        cacheFolder = new File(Config.getPath("addons/cache/"));


        if(cacheFolder.exists()){
            for (File f : Objects.requireNonNull(cacheFolder.listFiles())) {
                try {
                    cachedAddons.add(yaml.load(new InputStreamReader(Files.newInputStream(f.toPath()))));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        load();
    }

    public void load(){
        if(cacheFolder.exists()){
            boolean b =cacheFolder.delete();
            if(!b){
                Console.printLang("addons.loader.couldNotDeleteCache");
            }
        }

        dir = new File(Config.getPath("addons/"));

        try {
            if(!dir.exists()) {
                dir.mkdirs();
            }
            Console.printLang("addons.loading");
            if(isDirEmpty(dir.toPath())) {
                Console.printLang("addons.emptyFolder");
                return;
            }
            for(File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isDirectory())
                    continue;

                Console.printLang("addons.loadingAddon", file.getName());

                Addon cache = null;
                for (Addon module : cachedAddons) {
                    //System.out.println(file.toURI().toURL());
                    if (module.getUrl().toString().equals(file.toURI().toURL().toString())) {
                        cache = module;
                        Console.printLang("addons.cached", cache);
                        break;
                    }
                }
                CustomClassLoader child = new CustomClassLoader(
                        file.toURI().toURL(),
                        this.getClass().getClassLoader()
                );
                Addon addon = null;
                if(cache != null){
                   addon = cache;
                }else {
                    if(child.getResource("dreamy.yml") != null){
                        InputStream inputStream = child.getResourceAsStream("dreamy.yml");
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        byte[] prefix = "!!be.alexandre01.dreamnetwork.api.addons.Addon\n".getBytes();

                        byte[] finalBytes = new byte[prefix.length + bytes.length];
                        System.arraycopy(prefix, 0, finalBytes, 0, prefix.length);
                        System.arraycopy(bytes, 0, finalBytes, prefix.length, bytes.length);

                        addon = yaml.load(new String(finalBytes));

                        inputStream.close();
                    }
                }
                if(addon == null)
                    continue;

                addon.setFile(file);
                addon.setChild(child);
                addon.setUrl(file.toURI().toURL());
                Class<?> classToLoad = Class.forName(addon.getDreamyPath(), true, child);
                addon.setDefaultClass(classToLoad);
                this.addons.add(addon);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }



    public void searchAllObject(Object object){
        for(Field field : object.getClass().getFields()){
            searchForField(field,object);
        }
        for(Field field : object.getClass().getDeclaredFields()){
            searchForField(field,object);
        }
        for( Method method : object.getClass().getMethods()){
            try {
                searchAllObject(method.invoke(object));
            }catch (Exception ignored){

            }
        }

        for( Method method : object.getClass().getMethods()){
            try {
                searchAllObject(method.invoke(object));
            }catch (Exception ignored){

            }
        }
    }

    public void searchForField(Field field,Object object){
        if(field.getType() == List.class){
            try {
                List<?> l = (List<?>) field.get(object);
                for(Object o : l){
                    searchAllObject(o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(field.getType() == ArrayList.class){
            try {
                ArrayList<?> l = (ArrayList<?>) field.get(object);
                for(Object o : l){
                    searchAllObject(o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(field.getType() == HashMap.class){
            try {
                HashMap<?,?> l = (HashMap<?,?>) field.get(object);
                for(Object o : l.keySet()){
                    searchAllObject(o);
                }
                for(Object o : l.values()){
                    searchAllObject(o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        /*if(field.getType() == IPreset.class){
            try {
                field.setAccessible(true);
                field.set(object,Preset.instance.p);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(field.getType() == PresetData.class){
            try {
                field.setAccessible(true);
                field.set(object,Preset.instance.pData);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/
    }

    private boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}
