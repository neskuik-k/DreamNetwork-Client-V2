package be.alexandre01.dreamnetwork.core;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.IJVMUtils;
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

import be.alexandre01.dreamnetwork.api.commands.ICommandReader;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.service.JVMUtils;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ImplAPI extends DNCoreAPI {


    @Getter
    static DNCoreAPI instance;
    private final Core core;



    private final JVMUtils jvmUtils;

    IResponsesCollection responsesCollection;


    public ImplAPI(Core core){
       this.core = core;
       jvmUtils = new JVMUtils();
       instance = this;
       responsesCollection = new ResponsesCollection();
    }

    @Override
    public IClientManager getClientManager(){
        return core.getClientManager();
    }

    @Override
    public IDNChannelManager getChannelManager(){
        return core.getChannelManager();
    }

    @Override
    public PacketHandlingFactory getPacketFactory() {
        return core.getPacketHandlingFactory();
    }

    @Override
    public IContainer getContainer(){
        return core.getJvmContainer();
    }

    @Override
    public IJVMUtils getJVMUtils() {
        return jvmUtils;
    }

    @Override
    public IResponsesCollection getResponsesCollection() {
        return responsesCollection;
    }

    @Override
    public IServicePlayersManager getServicePlayersManager(){
        return core.getServicePlayersManager();
    }
    @Override
    public String getDevToolsToken(){
        return core.getDevToolsToken();
    }
    @Override
    public ArrayList<CoreReceiver> getGlobalResponses(){
        return core.getGlobalResponses();
    }

    @Override
    public Console getConsole(String name){
        return Console.getConsole( name );
    }

    @Override
    public IScreenManager getScreenManager(){
        return ScreenManager.instance;
    }
    @Override
    public ICommandReader getCommandReader(){
        return Main.getCommandReader();
    }

    @Override
    public EventsFactory getEventsFactory(){
        return core.getEventsFactory();
    }

    @Override
    public IBundleManager getBundleManager() {
        return core.getBundleManager();
    }

    @Override
    public IConfigManager getConfigManager() {
        return UtilsAPI.get().getConfigManager();
    }



    @Override
    public ICallbackManager getCallbackManager() {
        return core.getCallbackManager();
    }

    @Override
    public Optional<IExternalCore> getExternalCore() {
        return Optional.of(ExternalCore.getInstance());
    }

    @Override
    public void setLocalData(String key, Object data) {
        core.getDataLocalObjects().setLocalData(key,data);
    }

    @Override
    public Object getLocalData(String key) {
        return core.getDataLocalObjects().getLocalData(key);
    }

    @Override
    public <T> T getLocalData(String key, Class<T> tClass) {
        return core.getDataLocalObjects().getLocalData(key,tClass);
    }

    @Override
    public HashMap<String, Object> getLocalDatas() {
        return core.getDataLocalObjects().getLocalDatas();
    }

    @Override
    public Multimap<String, UniversalConnection> getDataSubscribers() {
        return core.getDataLocalObjects().getDataSubscribers();
    }


}
