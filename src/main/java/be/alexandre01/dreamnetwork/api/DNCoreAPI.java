package be.alexandre01.dreamnetwork.api;


import be.alexandre01.dreamnetwork.api.commands.CommandReader;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import lombok.Getter;

import java.util.ArrayList;

public class DNCoreAPI {

    @Getter
    static DNCoreAPI instance;
    private final Core core;



    public DNCoreAPI(){
        core = Core.getInstance();
        instance = this;
    }

    public IClientManager getClientManager(){
        return core.getClientManager();
    }

    public IDNChannelManager getChannelManager(){
        return core.getChannelManager();
    }

    public IContainer getContainer(){
        return core.getJvmContainer();
    }

    public IServicePlayersManager getServicePlayersManager(){
        return core.getServicePlayersManager();
    }
    public String getDevToolsToken(){
        return core.getDevToolsToken();
    }
    public ArrayList<CoreResponse> getGlobalResponses(){
        return core.getGlobalResponses();
    }

    public Console getConsole(String name){
        return Console.getConsole( name );
    }

    public IScreenManager getScreenManager(){
        return ScreenManager.instance;
    }

    public CommandReader getCommandReader(){
        return Main.getCommandReader();
    }

    public EventsFactory getEventsFactory(){
        return core.getEventsFactory();
    }




}
