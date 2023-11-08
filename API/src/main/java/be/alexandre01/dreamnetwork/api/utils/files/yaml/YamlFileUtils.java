package be.alexandre01.dreamnetwork.api.utils.files.yaml;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.console.Console;
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
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.IntStream;

public class YamlFileUtils<T> {

    final transient private List<String> annotations = new ArrayList<>();
    transient private Class<T> clazz;
    @Setter transient private Class toLoad;

    transient private T obj;
    transient @Getter File file;
    transient boolean skipNull;
    transient boolean ignorePatch;
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

    public boolean config(File file,Class<T> clazz,boolean skipNull,boolean ignorePatch){
        this.skipNull = skipNull;
        this.ignorePatch = ignorePatch;
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
    public boolean config(File file,Class<T> clazz,boolean skipNull){
        return config(file,clazz,skipNull,false);
    }
    public boolean config(File file,Class<T> clazz){
        return config(file,clazz,false,false);
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
        if(constructor == null || representer == null){
            yaml = new Yaml(constructor = new CustomConstructor(loaderOptions,ignorePatch,clazz),representer = new CustomRepresenter(skipNull,clazz)/*,representer*/);
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

        //System.out.println("Load as "+toLoad.getName());
        if(!ignorePatch){
            // System.out.println("Check if there is missing fields in the file of "+clazz.getName());
           // System.out.println(representer.getClass().getName());
           // System.out.println(representer);
            if(constructor instanceof CustomConstructor){
               // System.out.println("Check if there is missing fields in the file of "+clazz.getName());
                CustomConstructor c = (CustomConstructor) constructor;
                List<Field> l = new ArrayList<>();
                // add getDeclaredFields
                l.addAll(Arrays.asList(clazz.getDeclaredFields()));
               /* boolean isDifferent = l.stream()
                        .filter(field -> field.getAnnotation(Ignore.class) == null)
                        .filter(field -> field.isSynthetic())
                        .anyMatch(item -> !l.contains(item) || !c.getSettedFields().contains(item));*/

                for (Field field : l) {
                    // is Ignored
                    if(field.isAnnotationPresent(Ignore.class)) continue;
                    if(field.isAnnotationPresent(SkipInitCheck.class)) continue;
                    // is static
                    if(Modifier.isStatic(field.getModifiers())) continue;


                    //  System.out.println(field.getName());
                   // System.out.println(c.getSettedFields());
                    if(!c.getSettedFields().contains(field)){
                        //System.out.println("Missing field "+field.getName()+" in the file of "+clazz.getName());
                        throw new YAMLException("There is missing fields "+ field.getName()+" in the file of "+clazz.getName());
                    }
                }
                //if(isDifferent){
                  //  throw new RuntimeException("There is missing fields in the file of "+clazz.getName());
                //}
            }
        }


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
    public Optional<T> init(File file, boolean skipNull,boolean ignorePatch){
        if(!config(file,clazz,skipNull,ignorePatch)){
            obj = createObject();
            if(obj instanceof YamlPreLoader){
                ((YamlPreLoader) obj).whenLoaded();
            }
            return Optional.ofNullable(obj);
        }else {
            try {
                return Optional.ofNullable(obj = readObject());
            }catch (Exception e) {
                //System.out.println("Error reading file: "+file.getName());
                try {
                    if(!(e instanceof YAMLException)){
                        System.out.println("Error is an YamlException");
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
                        cantReplace.printStackTrace();
                        return null;
                    }
                    Console.printLang("core.utils.yaml.loadFileError", file.getName());
                    Console.bug(e,true);
                    return null;
                }
            }
        }
    }

    public Optional<T> init(File file, boolean skipNull){
        return init(file,skipNull,false);
    }



    private void fillInMap(List<String> list, Map<String,Object> map){
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if(s.length() > 1)
                if(s.charAt(1) == ' ') continue;
            while (s.startsWith(" ")){
                s = s.replaceFirst(" ","");
            }
            if(s.startsWith("#")) continue;
            if(s.contains(":")){
                String[] split = s.split(":");
                String key = split[0];

                if(split.length == 1 || split[1].replace(" ","").isEmpty()){

                    for (int j = 2; j < split.length; j++) {
                        split[1] = split[1]+":"+split[j];
                    }
                    //System.out.println("Split 2 total: "+split[1]);
                    System.out.println("Line split 2 detected");


                    ArrayList<String> listOfList = new ArrayList<>();
                    while (true){
                        if(list.size()-1 < i+1){
                            break;
                        }
                        String nextLine = list.get(i+1);
                        if(nextLine.contains(":")){
                            break;
                        }else {
                            if(nextLine.contains("-")){
                                listOfList.add(nextLine);
                                i++;
                            }
                        }
                    }
                    map.put(key,listOfList);
                }else {
                    while (split[1].startsWith(" ")){
                        split[1] = split[1].replaceFirst(" ","");
                    }

                    for (int j = 2; j < split.length; j++) {
                        split[1] = split[1]+":"+split[j];
                    }
                    System.out.println("Split 1 total: "+split[1]);
                    map.put(key,split[1]);
                }
            }
        }
    }
    public T replaceOldByNew() throws Exception{
        System.out.println("Replace old by new");
        ArrayList<String> linesBefore = new ArrayList<>();
        FileScan fileScan = new FileScan(getFile());
        fileScan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                if(line.contains(":")){
                    //  System.out.println("Line contains : "+line);
                    String[] split = line.split(":");
                    if(split.length == 1){
                        linesBefore.add(split[0].replace(" ","")+":");
                    }else {
                        for (int j = 2; j < split.length; j++) {
                            split[1] = split[1]+":"+split[j];
                        }
                        linesBefore.add(split[0]+":"+split[1]);
                    }
                    return;
                }
                linesBefore.add(line);
            }
        });

        createObject();

        ArrayList<String> linesAfter = new ArrayList<>();
        fileScan = new FileScan(getFile());
        fileScan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                if(line.contains(":")){
                  //  System.out.println("Line contains : "+line);
                    String[] split = line.split(":");
                    if(split.length == 1){
                        linesAfter.add(split[0].replace(" ","")+":");
                    }else {
                        for (int j = 1; j < split.length; j++) {
                            split[0] = split[0]+":"+split[j];
                            System.out.printf("Split %d: %s%n", j, split[j]);

                        }
                        System.out.println("Split total: "+split[0]);
                        linesAfter.add(split[0]);
                    }
                    return;
                }
                linesAfter.add(line);
            }
        });

        HashMap<String,Object> oldMap = new HashMap<>();
        HashMap<String,Object> newMap = new HashMap<>();

        fillInMap(linesBefore,oldMap);
        fillInMap(linesAfter,newMap);

        //System.out.println("Old map "+oldMap);
        //System.out.println("New map "+newMap);

        for (String s : newMap.keySet()) {
            if(oldMap.containsKey(s)){
                Object o = oldMap.get(s);
                if(o instanceof String){
                    //System.out.println(o);
                    //System.out.println(s+": "+oldMap.get(s));
                    int index = IntStream.range(0, linesAfter.size())
                            .filter(i -> {
                                System.out.printf("Check if %s is in %s%n", s+":", linesAfter.get(i));
                                return linesAfter.get(i).contains(s+":");
                            })
                            .findFirst().orElse(-1);
                    System.out.printf("Index of %s: %d%n", s+":", index);
                    linesAfter.set(index,s+": "+oldMap.get(s));
                }
                if( o instanceof List){
                    List<String> list = (List<String>) oldMap.get(s);
                    int index = linesAfter.indexOf(s+":");
                  //   System.out.println("List to add to index "+index);
                    linesAfter.set(index,s+":");

                    int sizeToOverride = newMap.get(s) instanceof List ? ((List) newMap.get(s)).size() : 0;


                    for (int i = 1; i <= list.size(); i++) {
                      if(sizeToOverride >= i){
                          linesAfter.set(index+i,list.get(i-1));
                        }else {
                          linesAfter.add(index+i,list.get(i-1));
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
            System.out.println("Try to save file "+file.getName());
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
            System.out.println("Save yml file: "+file.getName());
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
