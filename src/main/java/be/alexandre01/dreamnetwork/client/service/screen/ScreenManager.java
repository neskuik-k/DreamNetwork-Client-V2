package be.alexandre01.dreamnetwork.client.service.screen;



import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScreenManager implements be.alexandre01.dreamnetwork.api.service.screen.IScreenManager {
   @Getter private HashMap<String,IScreen> screens;
    @Getter private ArrayList<Integer> screenIds;
    @Getter private ArrayList<Integer> availableScreenIds;

   @Getter private HashMap<String,Integer> screenCurrentId;
    public static ScreenManager instance;

    public ScreenManager(){
        screens = new HashMap<>();
        screenCurrentId = new HashMap<>();
        availableScreenIds = new ArrayList<>();
        screenIds = new ArrayList<>();
    }
    @Override
    public int getId(IService service){
        return service.getId();

        /*
        if(!screenCurrentId.containsKey(processName)){
            return 0;
        }
        if(!availableScreenIds.isEmpty()){
            return availableScreenIds.get(0);
        }
       return screenCurrentId.get(processName)+1;*/
    }
    @Override
    public void addScreen(IScreen screen){
        System.out.println(Colors.YELLOW_BOLD+"Screen ouvert sous le nom de -> "+Colors.GREEN_BOLD+ screen.getService().getJvmExecutor().getName()+"-"+screen.getScreenId());
        screens.put(screen.getScreenName(), (Screen) screen);
        screenCurrentId.put(screen.getService().getJvmExecutor().getName(),screen.getScreenId());
        //remove if available screen is taken
        availableScreenIds.remove(screen.getScreenId());
        screenIds.add(screen.getScreenId());
    }
    @Override
    public void remScreen(IScreen screen){
        if(screens.containsValue(screen)){
            screens.remove(screen.getScreenName());
        }
        availableScreenIds.add(screen.getScreenId());
        screenIds.remove(screen.getScreenId());


    }
    @Override
    public boolean containsScreen(String s){
        return screens.containsKey(s);
    }
    @Override
    public void watch(String server){
        //Console.clearConsole();
        //A PATCH
       screens.get(server).getScreenStream().init(server,screens.get(server));
    }

    @Override
    public IScreen getScreen(String screenName) {
        return screens.get(screenName);
    }

    @Override
    public int getScreenId(String screenName) {
        return screenCurrentId.get(screenName);
    }
}
