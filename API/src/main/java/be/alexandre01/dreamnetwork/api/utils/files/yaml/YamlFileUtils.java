package be.alexandre01.dreamnetwork.api.utils.files.yaml;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.api.utils.files.FileScan;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public class YamlFileUtils<T> {

    final transient private List<String> annotations = new ArrayList<>();
    transient private Class<T> clazz;
    @Setter transient private Class toLoad;

    transient private T obj;
    transient @Getter File file;
    transient boolean skipNull;
    public transient DumperOptions dumperOptions = new DumperOptions();
    public transient LoaderOptions loaderOptions = new LoaderOptions();

    public transient Constructor constructor = null;

    public transient Representer representer = null;

    HashMap<Class<?>,Tag> tags = new HashMap<>();
     List<String> settedFields = new ArrayList<>();

     public YamlFileUtils(Class<T> clazz){
            this.clazz = clazz;
         // Ignore
     }
    public void addTag(Class<?> clazz, Tag tag) {
        tags.put(clazz, tag);
    }

    public boolean config(File file,Class<T> clazz,boolean skipNull){
        this.skipNull = skipNull;
        this.clazz = clazz;
        this.file = file;
        if(!file.exists()){
            try {
                file.createNewFile();
                return false;
            } catch (IOException e) {
                System.out.println("Error creating file: "+file.getName());
               // Console.printLang("core.utils.yaml.createFileError", file.getName());
                e.printStackTrace();
            }
        }
        return true;
    }

    public Object read() throws Exception{
        return loadFile(file, clazz);
    }

    public void saveFile(T obj){
        saveFile(file, obj,clazz,skipNull);
    }
    public void saveFile(){
        saveFile(file,obj,clazz,skipNull);
    }
    public Object loadFile(File file, Class<?> clazz) throws Exception{
      /* Representer representer = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {


                if (this.getClass().equals(property.getType())) {
                    return null;
                }
                else {
                    settedFields.add(property.getName());
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };*/
        Yaml yaml;
        if(constructor == null){
            yaml = new Yaml(new Constructor(loaderOptions),new CustomRepresenter(skipNull,null,clazz)/*,representer*/);
        }else {
            yaml = new Yaml(constructor,this.representer);
        }



           /* LinkedHashMap<String,Object> map = yaml.load(new FileInputStream(file));
            if(map == null){
                return null;
            }
            if(map.isEmpty()){
                return null;
            }*/

            Object t = null;

            // System.out.println("Load yml file: "+file.getName());
                Class<? super Object> toLoad;
                if(this.toLoad == null){
                    toLoad = (Class<? super Object>) clazz;
                }else {
                    toLoad = this.toLoad;
                }
                t = yaml.loadAs(Files.newInputStream(file.toPath()),toLoad);


           // Gson gson = new Gson();
            //T t = gson.fromJson(gson.toJsonTree(map), clazz);
          //  YamlFileUtils.this.readFile();
            return t;
    }

    public void preLoad(){
        addTag(clazz,Tag.MAP);
        representer = new CustomRepresenter(true,clazz);
        representer.addClassTag(clazz, Tag.MAP);
    }
    public Optional<T> init(File file, boolean skipNull){
        if(!config(file,clazz,skipNull)){
            obj = createObject();
            if(obj instanceof YamlPreLoader){
                ((YamlPreLoader) obj).whenLoaded();
            }
            return Optional.ofNullable(obj);
        }else {
            try {
                return Optional.ofNullable(obj = readObject());
            }catch (Exception e) {
                try {
                    if(!(e instanceof YAMLException)){
                        throw new RuntimeException(e);
                    }
                    System.out.println("Error reading file: "+file.getName());
                    System.out.println("Recreating the file and trying to put old data on it");
                    obj = replaceOldByNew();
                    if(obj instanceof YamlPreLoader){
                        ((YamlPreLoader) obj).whenLoaded();
                    }
                    return Optional.ofNullable(obj);
                }catch (Exception cantReplace){
                    if(DNUtils.get().getConfigManager().getLanguageManager() == null || Console.getFormatter() == null){
                        e.printStackTrace();
                        return null;
                    }
                    Console.printLang("core.utils.yaml.loadFileError", file.getName());
                    Console.bug(e,true);
                    return null;
                }
            }
        }
    }

    public T replaceOldByNew() throws Exception{
        ArrayList<String> linesBefore = new ArrayList<>();
        FileScan fileScan = new FileScan(getFile());
        fileScan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                linesBefore.add(line);
            }
        });

        createObject();

        ArrayList<String> linesAfter = new ArrayList<>();
        fileScan = new FileScan(getFile());
        fileScan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                linesAfter.add(line);
            }
        });
        int bIndex = 0;
        int aIndex = 0;
        int totalIndex = 0;

        for (int i = 0; i < Math.max(linesBefore.size(),linesAfter.size()); i++) {
            String oldLine = linesBefore.get(bIndex);
            String newLine = linesAfter.get(aIndex);

            if(oldLine.equalsIgnoreCase(newLine)){
                bIndex++;
                aIndex++;
                totalIndex++;
                continue;
            }

            if(oldLine.contains(":") && newLine.contains(":")){
                String[] oldSplit = oldLine.split(":");
                String[] newSplit = newLine.split(":");
                if(oldSplit[0].equalsIgnoreCase(newSplit[0])){
                    System.out.println("Replace "+newLine+" by "+oldLine);
                    linesAfter.set(totalIndex,oldLine);
                    bIndex++;
                    aIndex++;
                    totalIndex++;
                    continue;
                }
            }else {
                if(newLine.contains("-") && !oldLine.contains("-")  && linesAfter.size() > totalIndex+1 && linesBefore.size() > totalIndex+1){
                    String lastAfterLine = linesAfter.get(totalIndex-1);
                    String lastBeforeLine = linesBefore.get(totalIndex-1);

                    if(lastBeforeLine.equalsIgnoreCase(lastAfterLine)){
                        String[] newSplit = newLine.split("-");
                        String[] oldSplit = oldLine.split("-");
                        if(oldSplit[1].replace(" ","").equalsIgnoreCase(newSplit[1].replace(" ",""))){
                            System.out.println("Replace "+newLine+" by "+oldLine);
                            linesAfter.set(totalIndex,oldLine);
                            bIndex++;
                            aIndex++;
                            totalIndex++;
                            continue;
                        }
                    }

                }
                if(oldLine.contains("-")){
                    String[] oldSplit = oldLine.split("-");
                    if(!newLine.contains("-")){
                        System.out.println("Replace "+newLine+" by "+oldLine);
                        linesAfter.set(totalIndex,oldLine);
                        bIndex++;
                        totalIndex++;
                        continue;
                    }else {
                        String[] newSplit = newLine.split("-");
                        if(oldSplit[1].replace(" ","").equalsIgnoreCase(newSplit[1].replace(" ",""))){
                            System.out.println("Replace "+newLine+" by "+oldLine);
                            linesAfter.set(totalIndex,oldLine);
                            bIndex++;
                            aIndex++;
                            totalIndex++;
                            continue;
                        }
                    }
                }

            }
        }

        try {
            FileWriter fileWriter = new FileWriter(getFile());
            for (String s : linesAfter) {
                fileWriter.write(s+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readObject();
    }

    public T readObject() throws Exception{
        return (T) read();
    }

    public T createObject() {
         Class<T> clazz = (Class<T>) this.clazz;
        try {
            T t = clazz.newInstance();
            saveFile(t);
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            if(DNUtils.get().getConfigManager().getLanguageManager() == null || Console.getFormatter() == null){
                e.printStackTrace();
                return null;
            }
            Console.print("Error creating object: "+clazz.getName());
            Console.bug(e,true);
            return null;
        }
    }

    @Deprecated
    public void readAndReplace(Object o){
        Object config = null;
        try {
            config = read();
        } catch (Exception e) {
            return;
        }

        // Copy all data from config to this class
        // get declaredfields and fields
        if(config == null){
            return;
        }
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Field field1 = o.getClass().getDeclaredField(field.getName());
                field1.setAccessible(true);
                if(field1.getAnnotation(Ignore.class) != null) continue;
                field1.set(this,field.get(config));
                //Console.printLang("service.startupConfig.settingField", field.getName(), field.get(config));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveFile(File file, Object obj, Class<?> clazz, boolean skipNull){
        try {

            if(!file.exists())
                file.createNewFile();
            Representer representer;
            if(this.representer == null){
                representer = new CustomRepresenter(skipNull,obj,clazz);
            }else {
                representer = this.representer;
                if(representer instanceof CustomRepresenter){
                    ((CustomRepresenter) representer).setObj(obj);
                }
            }

            for (Class<?> aClass : tags.keySet()) {
                representer.addClassTag(aClass, tags.get(aClass));
            }
            Yaml yaml;

            if(constructor == null){
                yaml = new Yaml(representer,dumperOptions);
            }else {
                yaml = new Yaml(representer,dumperOptions);
                //yaml = new Yaml(constructor,representer,new DumperOptions());
            }



            FileWriter fileWriter = new FileWriter(file);
            if(!annotations.isEmpty()){
                for (String annotation : annotations) {
                    fileWriter.write(annotation);
                }
            }
            //substring to remove last \n new line
            String dump = yaml.dumpAsMap(obj);
            fileWriter.write(dump.substring(0,dump.length()-1));

            fileWriter.close();
        } catch (IOException e) {
            if(DNUtils.get().getConfigManager().getLanguageManager() == null){
                System.out.println("Error writing file: "+file.getName());
                e.printStackTrace();
                return;
            }
            Console.printLang("core.utils.yaml.errorWritingFile", file.getName());
            Console.bug(e);
        }
    }



    public void addAnnotation(String annotation){
        annotations.add("# "+annotation+"\n");
    }

}
