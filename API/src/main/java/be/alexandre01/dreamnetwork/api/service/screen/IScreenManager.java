package be.alexandre01.dreamnetwork.api.service.screen;

import be.alexandre01.dreamnetwork.api.service.IService;

import java.util.ArrayList;
import java.util.HashMap;

public interface IScreenManager {


    int getId(IService service);

    void addScreen(IScreen screen);

    void remScreen(IScreen screen);

    boolean containsScreen(String s);

    HashMap<String,IScreen> getScreens();

    ArrayList<Integer> getScreenIds();

    void watch(String server);

    IScreen getScreen(String screenName);

    int getScreenId(String screenName);



}
