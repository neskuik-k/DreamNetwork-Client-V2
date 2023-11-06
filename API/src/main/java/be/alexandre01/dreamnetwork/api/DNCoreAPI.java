package be.alexandre01.dreamnetwork.api;


import be.alexandre01.dreamnetwork.api.commands.ICommandReader;
import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IResponsesCollection;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.PacketHandlingFactory;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.connection.external.IExternalCore;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleManager;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;

import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public abstract class DNCoreAPI {

    @Getter
    static DNCoreAPI instance;

    public DNCoreAPI(){
        instance = this;
    }

    public abstract IClientManager getClientManager();

    public abstract IDNChannelManager getChannelManager();
    public abstract PacketHandlingFactory getPacketFactory();

    public abstract IContainer getContainer();
    public abstract IJVMUtils getJVMUtils();

    public abstract IResponsesCollection getResponsesCollection();

    public abstract IServicePlayersManager getServicePlayersManager();
    public abstract String getDevToolsToken();
    public abstract ArrayList<CoreReceiver> getGlobalResponses();

    public abstract Console getConsole(String name);

    public abstract IScreenManager getScreenManager();

    public abstract ICommandReader getCommandReader();

    public abstract EventsFactory getEventsFactory();

    public abstract IBundleManager getBundleManager();

    public abstract IConfigManager getConfigManager();


    public abstract ICallbackManager getCallbackManager();


    public abstract Optional<IExternalCore> getExternalCore();

    public abstract void setLocalData(String key, Object data);
    public abstract Object getLocalData(String key);
    public abstract <T> T getLocalData(String key, Class<T> tClass);
    public abstract HashMap<String, Object> getLocalDatas();
    public abstract Multimap<String, UniversalConnection> getDataSubscribers();
}
