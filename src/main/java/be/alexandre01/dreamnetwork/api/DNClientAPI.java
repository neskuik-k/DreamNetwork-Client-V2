package be.alexandre01.dreamnetwork.api;


import be.alexandre01.dreamnetwork.api.commands.CommandReader;
import be.alexandre01.dreamnetwork.api.commands.CommandsManager;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsolePath;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import lombok.Getter;

import java.util.ArrayList;

public class DNClientAPI {

    @Getter
    static DNClientAPI instance;
    private final Client client;



    public DNClientAPI(){
        client = Client.getInstance();
        instance = this;
    }

    public IClientManager getClientManager(){
        return client.getClientManager();
    }

    public IDNChannelManager getChannelManager(){
        return client.getChannelManager();
    }

    public IContainer getContainer(){
        return client.getJvmContainer();
    }

    public IServicePlayersManager getServicePlayersManager(){
        return client.getServicePlayersManager();
    }
    public String getDevToolsToken(){
        return client.getDevToolsToken();
    }
    public ArrayList<CoreResponse> getGlobalResponses(){
        return client.getGlobalResponses();
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
        return client.getEventsFactory();
    }




}
