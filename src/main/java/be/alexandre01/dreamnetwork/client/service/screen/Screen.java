package be.alexandre01.dreamnetwork.client.service.screen;

import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.stream.ScreenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class  Screen extends Thread {
    JVMService service;
    ArrayList<String> history;
    ScreenStream screenStream;
    ScheduledExecutorService executors;
    public Screen(JVMService service){
        this.service = service;
        this.history = new ArrayList<>();
        this.screenStream = new ScreenStream();
        ScreenManager.instance.addScreen(this);
        executors = Executors.newScheduledThreadPool(16);

    }

    @Override
    public void run() {

     /*  String data = null;
                Instant now = Instant.now();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(service.getProcess().getInputStream()));
                try {
                    while (service.getProcess().isAlive()){
                      //  if(Duration.between(now,Instant.now()).toMillis()<= 200){
                        while ((data = bufferedReader.readLine()) != null){
                            if(history.size() > 25){
                                history.remove(0);
                            }
                            history.add(data);
                            System.out.println(data);
                            if(screenStream.isInit){

                            }
                               // screenStream.out.println("HUMMM"+data);
                        }
                       /* }else {
                            break;
                        }

                    }
                    destroy();

            } catch (IOException e) {
                    e.printStackTrace();
                }*/

        System.out.println("yes");
    //    System.setIn(this.process.getInputStream());
        //PrintStream pr = new PrintStream(this.server.getProcessus().getOutputStream());
       // System.setOut(pr);
       /* String data = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getProcessus().getInputStream()));
            try {
            while (server.getProcessus().isAlive()){
               while ((data = bufferedReader.readLine()) != null){
                    if(history.size() > 25){
                        history.remove(0);
                    }
                    history.add(data);
                    if(screenStream.isInit)
                    screenStream.out.println(data);
                }
            }
            destroy();
        }catch (Exception e){

        }

*/
        System.out.println("it works");
    }

    public void destroy(){
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
