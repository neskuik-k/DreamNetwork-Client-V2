package be.alexandre01.dreamnetwork.core.service.screen;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.list.screens.CoreScreenCreateEvent;
import be.alexandre01.dreamnetwork.api.events.list.screens.CoreScreenDestroyEvent;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenStream;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.screen.stream.internal.ProcessScreenStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Getter @Setter
public class Screen extends Thread implements IScreen {
    IService service;
    ArrayList<UniversalConnection> devToolsReading = new ArrayList<>();
    IScreenStream screenStream;
    volatile Integer screenId;
    boolean running = true;
    String screenName;
    boolean viewing = false;

    /**
     * Creates a new instance of the Screen class with the given service.
     *
     * @param service the service to associate with the screen
     */
    public Screen(IService service){
        if(service.getUsedConfig().getScreenEnabled() == null){
            viewing = true;
        }else{
            if(service.getUsedConfig().getScreenEnabled()){
                viewing = true;
            }
        }
        this.service = service;


        ScreenManager screenManager = ScreenManager.instance;
        screenId = getService().getIndexingId();
        screenName = getService().getFullIndexedName();//service.getJvmExecutor().getBundleData().getName()+"/"+service.getJvmExecutor().getName()+"-"+screenId;
        if(viewing){
            this.screenStream = new ProcessScreenStream(screenName,this);
        }
        service.setScreen(this);
        screenManager.addScreen(this);
        Core core = Core.getInstance();
        core.getEventsFactory().callEvent(new CoreScreenCreateEvent(core.getDnCoreAPI(),this));
    }

    @Override
    public void run() {
    }


    @Override
    public synchronized void destroy(boolean fromService){
        if(!running){
            return;
        }

        //close logging
        if(getScreenStream() != null && getScreenStream().getScreenInReader() != null && getScreenStream().getScreenInReader().getFileHandler() != null)
            getScreenStream().getScreenInReader().getFileHandler().close();
        running = false;
        if(Console.actualConsole.equals("s:"+screenName)){
            Console.setActualConsole("m:default");
            Console.getConsole("s:"+screenName).destroy();
            System.out.println("The PROCESS "+service.getExecutor().getName()+" has just killed himself.");
        }



        ScreenManager.instance.remScreen(this);
        //if(viewing)
        // screenStream.exit();

        /*if(getService().getClient() == null){
            getService().removeService();
        }*/

        //If not executed by service => Stop

        if(!fromService){
            service.stop();
        }


        Core core = Core.getInstance();
        core.getEventsFactory().callEvent(new CoreScreenDestroyEvent(core.getDnCoreAPI(),this));
    }

    @Override
    public IService getService() {
        return service;
    }

  

    @Override
    public IScreenStream getScreenStream() {
        return screenStream;
    }
}
