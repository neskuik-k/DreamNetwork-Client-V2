package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.console.IConsoleManager;
import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.api.console.colors.ColorsData;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import lombok.Getter;
import lombok.Setter;

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

    @Getter @Setter
    public ColorsData colorsData;

    public abstract AccessibilityMenu createAccessibilityMenu();
    public abstract IScreen createScreen(IService service);
    public abstract ICallbackManager createCallbackManager();
    public abstract IRequestManager createClientRequestManager(UniversalConnection client);
    public abstract IConfigManager getConfigManager();
    public abstract IConsoleManager getConsoleManager();

}
