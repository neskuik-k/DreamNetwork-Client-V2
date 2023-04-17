package be.alexandre01.dreamnetwork.core.utils.files.yaml;

import be.alexandre01.dreamnetwork.core.console.Console;
import com.google.gson.Gson;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class YamlFileUtils<T> {

    private List<String> annotations = new ArrayList<>();
    private Class<T> clazz;
    @Getter File file;
     boolean skipNull;

     List<String> settedFields = new ArrayList<>();


    public boolean config(File file,Class<T> clazz,boolean skipNull){
        this.skipNull = skipNull;
        this.clazz = clazz;
        this.file = file;
        if(!file.exists()){
            try {
                file.createNewFile();
                return false;
            } catch (IOException e) {
                Console.printLang("core.utils.yaml.createFileError", file.getName());
                e.printStackTrace();
            }
        }
        return true;
    }

    public T read(){
        return (T) loadFile(file, clazz);
    }

    public void saveFile(T obj){
        saveFile(file, obj,clazz,skipNull);
    }
    public void saveFile(){
        saveFile(file,this,clazz,skipNull);
    }
    public T loadFile(File file, Class<T> clazz){
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
        Yaml yaml = new Yaml(new SafeConstructor()/*,representer*/);

        try {
            LinkedHashMap<String,Object> map = yaml.load(new FileInputStream(file));
            if(map.isEmpty()){
                return null;
            }
            Gson gson = new Gson();
            T t = gson.fromJson(gson.toJsonTree(map), clazz);
          //  YamlFileUtils.this.readFile();
            return t;
        } catch (FileNotFoundException e) {
            Console.printLang("core.utils.yaml.loadFileError", file.getName());
            e.printStackTrace();
            return null;
        }
    }

    public void readAndReplace(Object o){
        Object config = read();

        // Copy all data from config to this class
        // get declaredfields and fields

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


            Representer representer = new Representer() {
                @Override
                protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {

                  /*  System.out.println(property.getType());
                    System.out.println(propertyValue);
*/


                    //check if field has annotation @Ignore


                    if(propertyValue == null && skipNull){
                        return null;
                    }
                    Field[] fields = clazz.getDeclaredFields();
                    boolean isFinded = false;
                    for (Field field : fields) {
                        field.setAccessible(true);

                    //    System.out.println("Annotation => "+field.getAnnotation(Ignore.class));
                          if (field.getAnnotation(Ignore.class) != null){
                                   // Console.printLang("warning");
                                    /*System.out.println("WARNING");
                                    System.out.println(field.getName());
                                    System.out.println(property.getName());
                                    System.out.println(field.get(obj));*/
                                    if(field.getName().equals(property.getName())){
                                        //System.out.println("IGNORED field "+field.getName()+" because it's equals to "+property.getName());
                                       // Console.printLang("core.utils.yaml.ignoreFieldEquals", field.getName(), propertyValue);
                                        return null;
                                    }
                            }

                          if(field.getName().equals(property.getName())){
                              isFinded = true;
                          }
                    }

                    if(!isFinded){
                        Console.fine(Console.getFromLang("core.utils.yaml.ignoreFieldNotFound", property.getName(), clazz.getName()));
                        return null;
                    }
                    if (obj.getClass().equals(property.getType())) {
                        return null;
                    }
                    else {
                        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                    }
                }
            };
            Yaml yaml = new Yaml(new Constructor(clazz),representer);

            FileWriter fileWriter = new FileWriter(file);
            if(!annotations.isEmpty()){
                for (String annotation : annotations) {
                    fileWriter.write(annotation);
                }
            }
            fileWriter.write(yaml.dumpAsMap(obj));
            fileWriter.flush();
            fileWriter.close();


        } catch (IOException e) {
            Console.printLang("core.utils.yaml.errorWritingFile", file.getName());
            Console.bug(e);
        }
    }

    public void addAnnotation(String annotation){
        annotations.add("# "+annotation+"\n");
    }

}
