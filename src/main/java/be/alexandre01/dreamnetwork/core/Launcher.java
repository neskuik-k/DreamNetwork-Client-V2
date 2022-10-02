package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.core.libraries.DownloadLibraries;
import be.alexandre01.dreamnetwork.core.libraries.LoadLibraries;

public class Launcher {
    public static void main(String... args){
        try{
            Class<?> clazz = Class.forName("com.google.gson.Gson");
            try {
                Main.main(args);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }catch(ClassNotFoundException e){
            new DownloadLibraries().init();
            new LoadLibraries().init(args);
        }
    }
}
