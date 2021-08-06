package be.alexandre01.dreamnetwork.client.service.screen;

import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.stream.ScreenStream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@EqualsAndHashCode(callSuper = true)
@Data
public class  Screen extends Thread {
    JVMService service;
    ArrayList<String> history;
    ScreenStream screenStream;
    Integer screenId;
    String screenName;

    public Screen(JVMService service){
        this.service = service;
        this.history = new ArrayList<>();
        ScreenManager screenManager = ScreenManager.instance;
        screenId = screenManager.getId(service.getJvmExecutor().getName());
        screenName = service.getJvmExecutor().getName()+"-"+screenId;
        this.screenStream = new ScreenStream(screenName,this);
        screenManager.addScreen(this);
    }

    @Override
    public void run() {
    }

    public void destroy(){
        System.out.println();
        if(Console.actualConsole.equals("s:"+screenName)){
            Console.setActualConsole("m:default");
            System.out.println("Le PROCESSUS "+service.getJvmExecutor().getName()+" vient de se tuer.");
        }
        ScreenManager.instance.remScreen(this);
        screenStream.exit();
    }

    public JVMService getService() {
        return service;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public ScreenStream getScreenStream() {
        return screenStream;
    }
}
