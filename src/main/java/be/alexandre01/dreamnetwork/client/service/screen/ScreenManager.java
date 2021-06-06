package be.alexandre01.dreamnetwork.client.service.screen;



import be.alexandre01.dreamnetwork.client.console.Console;
import lombok.Getter;

import java.util.HashMap;

public class ScreenManager {
   @Getter private HashMap<String,Screen> screens;
   @Getter private HashMap<String,Integer> screenCurrentId;
    public static ScreenManager instance;

    public static void load(){
        if(instance==null)
            instance = new ScreenManager();

    }
    public ScreenManager(){
        screens = new HashMap<>();
        screenCurrentId = new HashMap<>();
    }
    public int getId(String processName){
        if(!screenCurrentId.containsKey(processName)){
            return 0;
        }
       return screenCurrentId.get(processName);
    }
    public void addScreen(Screen screen){
        System.out.println("Screen name -> "+ screen.service.getJvmExecutor().getName()+"-"+screen.getService().getId());
        screens.put(screen.screenName,screen);
        screenCurrentId.put(screen.getService().getJvmExecutor().getName(),screen.screenId+1);
    }
    public void remScreen(Screen screen){
        screens.remove(screen.screenName);
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
