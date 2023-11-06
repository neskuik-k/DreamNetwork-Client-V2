package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassFinder {

    public Set<Class> findAllClassesFrom(String packageName,Class<?>... ignoreClass) {
        System.out.println("Find all classes from "+ packageName);
        InputStream stream = ClassLoader.getSystemClassLoader()
          .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        System.out.println("?");
        System.out.println(stream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        System.out.println("Test stream");
        System.out.println(stream);
        reader.lines().forEach(System.out::println);
        return reader.lines()
          .filter(line -> line.endsWith(".class"))
          .filter(line -> {
              System.out.println("Filter > "+line);
                for(Class<?> clazz : ignoreClass){
                    if(line.contains(clazz.getSimpleName())){
                        return false;
                    }
                }
                return true;
            })
          .map(line -> getClass(line, packageName))
          .collect(Collectors.toSet());
    }
 
    private Class getClass(String className, String packageName) {
        try {
            System.out.println("Finded class "+ className + " in package "+ packageName);
            return Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }
}