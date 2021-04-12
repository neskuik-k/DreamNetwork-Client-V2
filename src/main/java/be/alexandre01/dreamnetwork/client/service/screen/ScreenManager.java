package be.alexandre01.dreamnetwork.client.service.screen;



import be.alexandre01.dreamnetwork.client.console.Console;
import lombok.Getter;

import java.util.HashMap;

public class ScreenManager {
   @Getter private HashMap<String,Screen> screens;
    public static ScreenManager instance;

    public static void load(){
        if(instance==null)
            instance = new ScreenManager();

    }
    public ScreenManager(){
        screens = new HashMap<>();
    }
    public void addScreen(Screen screen){
        System.out.println("Screen name -> "+ screen.service.getJvmExecutor().getName()+"-"+screen.getService().getId());
        screens.put(screen.service.getJvmExecutor().getName()+"-"+screen.getService().getId(),screen);
    }
    public void remScreen(Screen screen){
        screens.remove(screen.service.getJvmExecutor().getName()+"-"+screen.getService().getId());
    }
    public boolean containsScreen(String s){
        return screens.containsKey(s);
    }
    public void watch(String server){
        //Console.clearConsole();
        //A PATCH
       screens.get(server).screenStream.init(server,screens.get(server));
    }
}
