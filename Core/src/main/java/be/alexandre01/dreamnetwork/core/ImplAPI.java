package be.alexandre01.dreamnetwork.core;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.IJVMUtils;
import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IResponsesCollection;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.connection.external.IExternalCore;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleManager;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;

import be.alexandre01.dreamnetwork.api.commands.ICommandReader;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.service.JVMUtils;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Optional;

public class ImplAPI extends DNCoreAPI {


    @Getter
    static DNCoreAPI instance;
    private final Core core;



    private final JVMUtils jvmUtils;


    public ImplAPI(){
        core = Core.getInstance();
        jvmUtils = new JVMUtils();
        instance = this;
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
    public IContainer getContainer(){
        return core.getJvmContainer();
    }

    @Override
    public IJVMUtils getJVMUtils() {
        return jvmUtils;
    }

    @Override
    public IResponsesCollection getResponsesCollection() {
        return new ResponsesCollection();
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
    public ArrayList<CoreResponse> getGlobalResponses(){
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
    public ICoreHandler getCoreHandler() {
        return Core.getInstance().getCoreHandler();
    }

    @Override
    public ICallbackManager getCallbackManager() {
        return Core.getInstance().getCallbackManager();
    }

    @Override
    public Optional<IExternalCore> getExternalCore() {
        return Optional.of(ExternalCore.getInstance());
    }


}
