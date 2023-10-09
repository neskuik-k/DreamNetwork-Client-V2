package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.core.console.language.ThemesMap;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {

    public static UtilsAPI utilsAPI;
    public static ThemesMap themesMap;
    public static void main(String... args){
        try{
            Class<?> clazz = Class.forName("com.google.gson.Gson");
            try {
                System.setProperty("illegal-access", "permit");
                System.setProperty("file.encoding", "UTF-8");

                Logger.getLogger("org.yaml.snakeyaml").setLevel(Level.OFF);
                utilsAPI = new UtilsAPI();
                themesMap = new ThemesMap();
                Main.main(args);

                Main.setUtilsAPI(utilsAPI);
                Main.setThemesMap(themesMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }catch(ClassNotFoundException e){
            System.out.println("No Library");
            System.out.println("You have to use DNLauncher.jar (>= 1.0.3 version)");
            //new DownloadLibraries().init();
            //new LoadLibraries().init(args);
        }
    }
}
