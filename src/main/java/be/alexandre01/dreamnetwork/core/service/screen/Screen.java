package be.alexandre01.dreamnetwork.core.service.screen;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.events.list.screens.CoreScreenCreateEvent;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.screen.stream.ScreenStream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class Screen extends Thread implements IScreen {
    IService service;
    ArrayList<String> history;
    ArrayList<IClient> devToolsReading = new ArrayList<>();
    ScreenStream screenStream;
    volatile Integer screenId;
    String screenName;

    public Screen(IService service){
        this.service = service;
        this.history = new ArrayList<>();
        ScreenManager screenManager = ScreenManager.instance;
        screenId = screenManager.getId(service);
        screenName = service.getJvmExecutor().getBundleData().getName()+"/"+service.getJvmExecutor().getName()+"-"+screenId;
        this.screenStream = new ScreenStream(screenName,this);
        service.setScreen(this);
        screenManager.addScreen(this);
        Core core = Core.getInstance();
        core.getEventsFactory().callEvent(new CoreScreenCreateEvent(core.getDnCoreAPI(),this));
    }

    @Override
    public void run() {
    }


    @Override
    public synchronized void destroy(){
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
            Config.removeDir("/runtimes/"+ getService().getJvmExecutor().getBundleData().getName() + "/"+ getService().getJvmExecutor().getName()+"/"+getService().getJvmExecutor().getName()+"-"+getService().getId());
        }
        Core core = Core.getInstance();
        core.getEventsFactory().callEvent(new CoreScreenCreateEvent(core.getDnCoreAPI(),this));
    }

    @Override
    public IService getService() {
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
