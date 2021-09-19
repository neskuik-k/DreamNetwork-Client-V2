package be.alexandre01.dreamnetwork.client.libraries;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;

import org.apache.commons.lang.exception.NestableRuntimeException;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.jar.JarFile;

public class LoadLibraries{
    private File dir;
    public static MyClassloader classloader;
    public void init(String[] args){
        try {


                dir = new File("libs");
                if(!dir.exists())
                    return;
            System.out.println(Thread.currentThread().getContextClassLoader());
            ArrayList<URL> urls = new ArrayList<>();
            MyClassloader classLoader = new MyClassloader(new URL[0], Main.class.getClassLoader());
            LoadLibraries.classloader = classLoader;
                for(File file : Objects.requireNonNull(dir.listFiles())){
                    CustomClassLoader customClassLoader = new CustomClassLoader(file.toURI().toURL());
                    urls.add(file.toURI().toURL());
                    if(file.isDirectory())
                        continue;

                   // ClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, Client.class.getClassLoader());
                 /*   CustomClassLoader child = new CustomClassLoader(
                            file.toURI().toURL(),
                            this.getClass().getClassLoader()
                    );*/






                   /* Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(classLoader, file.toURI().toURL());*/
                   classLoader.addURL(file.toURI().toURL());
                    if(file.getName().contains("jline")){
                        System.out.println("YES");
                        System.out.println(  Class.forName("jline.console.ConsoleReader", true, classLoader).getName());
                    }





                    System.out.println("loaded "+file.getName());
                    System.out.println(file.toURI().toURL());

                }

                ClassLoader classloader = SecureClassLoader.getSystemClassLoader() ;
                URL[] url = new URL[urls.size()];
                urls.toArray(url);
                ClassLoader customClassLoader = new URLClassLoader(urls.toArray(new URL[0]));

            System.out.println(Arrays.toString(urls.toArray(new URL[0])));;

                Class<?> clazz = customClassLoader.loadClass("be.alexandre01.dreamnetwork.client.Main");
            Method method = clazz.getMethod("main", String[].class);

            Thread thread = new Thread(() -> {
                try {
                    System.out.println("Hello");
                    method.invoke(null, (Object) args);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }, "Application-Thread");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setContextClassLoader(classLoader);
            thread.start();


            /*ConsoleReader consoleReader = new ConsoleReader();
            System.out.println(consoleReader);*/

            System.out.println(Thread.currentThread().getContextClassLoader().getClass());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    static void setAccessible(final AccessibleObject ao,
                              final boolean accessible) {
        if (System.getSecurityManager() == null)
            ao.setAccessible(accessible); // <~ Dragons
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    ao.setAccessible(accessible);
                    return null;
                }
            });
        }
    }

    public static Object invokeClass(String className, Object... objects){
        Class<?> clazz = null;
        System.out.println(className);
        try {
            clazz = Class.forName(className,true,classloader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(objects.length == 0){
            System.out.println("Wtf");
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            try {
                return clazz.getDeclaredConstructor(Arrays.stream(objects).map(Object::getClass).toArray(Class<?>[]::new)).newInstance(objects);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
