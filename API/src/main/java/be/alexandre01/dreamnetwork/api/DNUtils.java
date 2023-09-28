package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.console.IConsoleManager;
import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 19:29
*/
public abstract class DNUtils {
    static DNUtils instance;
    public DNUtils(){
        instance = this;
    }
    public static DNUtils get(){
        return instance;
    }
    public abstract AccessibilityMenu createAccessibilityMenu();
    public abstract IConfigManager getConfigManager();
    public abstract IConsoleManager getConsoleManager();

}
