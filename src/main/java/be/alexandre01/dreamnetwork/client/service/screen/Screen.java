package be.alexandre01.dreamnetwork.client.service.screen;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.events.list.screens.CoreScreenCreateEvent;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.stream.ScreenStream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class  Screen extends Thread implements IScreen {
    JVMService service;
    ArrayList<String> history;
    ArrayList<IClient> devToolsReading = new ArrayList<>();
    ScreenStream screenStream;
    volatile Integer screenId;
    String screenName;

    public Screen(JVMService service){
        this.service = service;
        this.history = new ArrayList<>();
        ScreenManager screenManager = ScreenManager.instance;
        screenId = screenManager.getId(service);
        screenName = service.getJvmExecutor().getName()+"-"+screenId;
        this.screenStream = new ScreenStream(screenName,this);
        service.setScreen(this);
        screenManager.addScreen(this);
        be.alexandre01.dreamnetwork.client.Client client = be.alexandre01.dreamnetwork.client.Client.getInstance();
        client.getEventsFactory().callEvent(new CoreScreenCreateEvent(client.getDnClientAPI(),this));
    }

    @Override
    public void run() {
    }


    @Override
    public void destroy(){
        if(Console.actualConsole.equals("s:"+screenName)){
            Console.getConsole("s:"+screenName).destroy();
            Console.setActualConsole("m:default");
            System.out.println("The PROCESS "+service.getJvmExecutor().getName()+" has just killed himself.");

        }
        ScreenManager.instance.remScreen(this);
        screenStream.exit();
        if(getService().getClient() == null){
            getService().removeService();
        }


        if(getService().getJvmExecutor().getType() == JVMExecutor.Mods.DYNAMIC){
            String t = getService().getJvmExecutor().isProxy() ? "proxy" : "server";
            Config.removeDir("/tmp/"+ t + "/"+ getService().getJvmExecutor().getName()+"/"+getService().getJvmExecutor().getName()+"-"+getService().getId());
        }
        be.alexandre01.dreamnetwork.client.Client client = be.alexandre01.dreamnetwork.client.Client.getInstance();
        client.getEventsFactory().callEvent(new CoreScreenCreateEvent(client.getDnClientAPI(),this));
    }

    @Override
    public JVMService getService() {
        return service;
    }

    @Override
    public ArrayList<String> getHistory() {
        return history;
    }

    @Override
    public ScreenStream getScreenStream() {
        return screenStream;
    }
}
