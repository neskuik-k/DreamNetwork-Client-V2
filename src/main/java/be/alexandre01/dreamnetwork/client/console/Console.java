package be.alexandre01.dreamnetwork.client.console;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Console extends Thread{
    public interface IConsole{
        public void listener(String[] args);
    }
    IConsole iConsole;
    private static final HashMap<String, Console> instances = new HashMap<>();
    private ConsoleReader consoleReader = new ConsoleReader();
    public String name;
    private final ArrayList<ConsoleMessage> history;
    public static String defaultConsole;
    public static String actualConsole;

    public static Console load(String name){
        Console c = new Console(name);
        instances.put(name,c);
        new Thread(c).start();
        return c;
    }
    public static void setActualConsole(String name){
        if(actualConsole != null){
            Console oldConsole = instances.get(actualConsole);
             oldConsole.stop();
        }

        Console console = instances.get(name);
        Console.actualConsole = name;
        if(!console.history.isEmpty()){
            for (ConsoleMessage s : console.history){
                console.forcePrint(s.content,s.level);
            }
        }
        if(!console.isAlive()){
            new Thread(console).start();
        }



    }

    public static void setDefaultConsole(String defaultConsole) {
        if(Console.defaultConsole != null)
            instances.get(defaultConsole).stop();
        Console.defaultConsole = defaultConsole;
    }

    public static Console getConsole(String name) {
        return instances.get(name);
    }

    public static void print(Object s, Level level){
        instances.get("m:default").fPrint(s+Colors.ANSI_RESET(),level);
    }
    public static void print(String s, Level level,String name){
        instances.get(name).fPrint(s + Colors.ANSI_RESET(),level);
    }
    private void forcePrint(String s,Level level){
        if(Console.actualConsole.equals(name))
            Client.getLogger().info(s+Colors.ANSI_RESET());
    }
    public void fPrint(Object s,Level level){
        if(Console.actualConsole.equals(name)){
            Client.getLogger().log(level,s+Colors.ANSI_RESET());
        }


        refreshHistory(s + Colors.ANSI_RESET(),level);
    }
    public static void print(Object s){
       System.out.println(s+Colors.ANSI_RESET());
    }

    public static void debugPrint(Object s){
        Client.getInstance().formatter.getDefaultStream().println(s+Colors.ANSI_RESET());
    }
    public static void clearConsole(){
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception ignored)
        {

        }
    }
    
    public void setConsoleAction(IConsole iConsole){
        this.iConsole = iConsole;
    }
    public Console(String name){
        this.history = new ArrayList<>();
        this.name = name;
    }
    @Override
    public void run() {
        try {
            while (true){
                if(actualConsole == null || !instances.containsKey(actualConsole))
                    actualConsole = defaultConsole;

                if(Console.actualConsole.equals(name)){

                String[] args = new String[0];
                try {
                    args = consoleReader.reader.readLine().split(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    iConsole.listener(args);
                //debugPrint("test >");
                   write("> ");

                }
            }
        }catch (Exception e){

        }
    }

    public ArrayList<ConsoleMessage> getHistory() {
        return history;
    }
    public void refreshHistory(String data,Level lvl){
        if(history.size() >= 25){
            history.remove(0);
        }
        //debugPrint("history >> "+data);
        history.add(new ConsoleMessage(data,lvl));
    }

    public void destroy(){
        history.clear();
        instances.remove(name);
    }

    public void write(String str){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Client.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
                    scheduler.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },50,50, TimeUnit.MILLISECONDS);

    }
    public static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }
}