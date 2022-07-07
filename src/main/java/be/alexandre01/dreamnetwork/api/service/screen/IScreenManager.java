package be.alexandre01.dreamnetwork.api.service.screen;

import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;

public interface IScreenManager {
    static void load() {
        if (ScreenManager.instance == null)
            ScreenManager.instance = new ScreenManager();
    }

    int getId(IService service);

    void addScreen(IScreen screen);

    void remScreen(IScreen screen);

    boolean containsScreen(String s);

    void watch(String server);

    IScreen getScreen(String screenName);

    int getScreenId(String screenName);

}
