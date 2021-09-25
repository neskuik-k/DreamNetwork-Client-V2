package be.alexandre01.dreamnetwork.client.service.screen;



import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScreenManager {
   @Getter private HashMap<String,Screen> screens;
    @Getter private ArrayList<Integer> screenIds;
    @Getter private ArrayList<Integer> availableScreenIds;

   @Getter private HashMap<String,Integer> screenCurrentId;
    public static ScreenManager instance;

    public static void load(){
        if(instance==null)
            instance = new ScreenManager();

    }
    public ScreenManager(){
        screens = new HashMap<>();
        screenCurrentId = new HashMap<>();
        availableScreenIds = new ArrayList<>();
        screenIds = new ArrayList<>();
    }
    public int getId(String processName){
        if(!screenCurrentId.containsKey(processName)){
            return 0;
        }
        if(!availableScreenIds.isEmpty()){
            return availableScreenIds.get(0);
        }
       return screenCurrentId.get(processName)+1;
    }
    public void addScreen(Screen screen){
        System.out.println(Colors.YELLOW_BOLD+"Screen ouvert sous le nom de -> "+Colors.GREEN_BOLD+ screen.service.getJvmExecutor().getName()+"-"+screen.screenId);
        screens.put(screen.screenName,screen);
        screenCurrentId.put(screen.getService().getJvmExecutor().getName(),screen.screenId);
        //remove if available screen is taken
        availableScreenIds.remove(screen.screenId);
        screenIds.add(screen.screenId);
    }
    public void remScreen(Screen screen){
        screens.remove(screen.screenName);
        availableScreenIds.add(screen.screenId);
        screenIds.remove(screen.screenId);


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
