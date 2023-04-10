package be.alexandre01.dreamnetwork.core.utils.files.yaml;

import be.alexandre01.dreamnetwork.core.console.Console;
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

public class YamlFileUtils<T> {


    private Class<T> clazz;
    @Getter File file;
     boolean skipNull;


    public void config(File file,Class<T> clazz,boolean skipNull){
        this.skipNull = skipNull;
        this.clazz = clazz;
        this.file = file;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Console.printLang("core.utils.yaml.createFileError", file.getName());
                e.printStackTrace();
            }
        }
    }

    public T read(){
        return (T) loadFile(file, clazz);
    }

    public void readFile(T obj){
        readFile(file, obj,clazz,skipNull);

    }
    public void readFile(){
        System.out.println(clazz);
        readFile(file,this,clazz,skipNull);
    }
    public T loadFile(File file, Class<T> clazz){
        Yaml yaml = new Yaml(new Constructor(clazz));
        try {
            return yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Console.printLang("core.utils.yaml.loadFileError", file.getName());
            e.printStackTrace();
            return null;
        }
    }
    public static void readFile(File file, Object obj, Class<?> clazz, boolean skipNull){
        try {

            file.createNewFile();
            Representer representer = new Representer() {
                @Override
                protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {

                    System.out.println(property.getType());
                    System.out.println(propertyValue);

                    //check if field has annotation @Ignore


                    if(propertyValue == null && skipNull){
                        return null;
                    }
                    Field[] fields = clazz.getDeclaredFields();
                    boolean isFinded = false;
                    for (Field field : fields) {
                        field.setAccessible(true);

                  //      System.out.println("Check => "+field.getName() );
                    //    System.out.println("Annotation => "+field.getAnnotation(Ignore.class));
                          if (field.getAnnotation(Ignore.class) != null){
                                try {
                                    Console.printLang("warning");
                                    System.out.println(field.getName());
                                    System.out.println(property.getName());
                                    System.out.println(field.get(obj));
                                    if(field.getName().equals(property.getName())){
                                        Console.printLang("core.utils.yaml.ignoreFieldEquals", field.getName(), propertyValue);
                                        return null;
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                          if(field.getName().equals(property.getName())){
                              isFinded = true;
                          }
                    }

                    if(!isFinded){
                        Console.debugPrint(Console.getFromLang("core.utils.yaml.ignoreFieldNotFound", property.getName(), clazz.getName()));
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
            Yaml yaml = new Yaml(new SafeConstructor(),representer);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(yaml.dumpAsMap(obj));
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            Console.printLang("core.utils.yaml.errorWritingFile", file.getName());
            Console.bug(e);
        }
    }

}
