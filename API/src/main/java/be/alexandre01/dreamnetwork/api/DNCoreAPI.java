package be.alexandre01.dreamnetwork.api;


import be.alexandre01.dreamnetwork.api.commands.ICommandReader;
import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleManager;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;

import lombok.Getter;

import java.util.ArrayList;

public abstract class DNCoreAPI {

    @Getter
    static DNCoreAPI instance;

    public DNCoreAPI(){
        instance = this;
    }

    public abstract IClientManager getClientManager();

    public abstract IDNChannelManager getChannelManager();

    public abstract IContainer getContainer();
    public abstract IJVMUtils getJVMUtils();

    public abstract IServicePlayersManager getServicePlayersManager();
    public abstract String getDevToolsToken();
    public abstract ArrayList<CoreResponse> getGlobalResponses();

    public abstract Console getConsole(String name);

    public abstract IScreenManager getScreenManager();

    public abstract ICommandReader getCommandReader();

    public abstract EventsFactory getEventsFactory();

    public abstract IBundleManager getBundleManager();

    public abstract IConfigManager getConfigManager();


}
