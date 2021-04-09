package be.alexandre01.dreamnetwork.client.console;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Config;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;
import com.google.common.collect.Ordering;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console extends Thread{
    public interface IConsole{
        public void listener(String[] args);
        public void consoleChange();
    }
    IConsole iConsole;
    private static final HashMap<String, Console> instances = new HashMap<>();
    private static ConsoleReader consoleReader = new ConsoleReader();
    public String name;
    private final ArrayList<ConsoleMessage> history;
    public static String defaultConsole;
    public static String actualConsole;
    public boolean isRunning = false;
    public String writing = "> ";

    public static Console load(String name){
        Console c = new Console(name);
        instances.put(name,c);
        new Thread(c).start();
        return c;
    }
    public static void setActualConsole(String name){
        if(actualConsole != null){
            Console oldConsole = instances.get(actualConsole);
            oldConsole.isRunning = false;
            oldConsole.stop();
            oldConsole.interrupt();

        }

        Console console = instances.get(name);
        Console.actualConsole = name;
        console.isRunning = true;
        if(!console.history.isEmpty()){
            List<ConsoleMessage> h = new ArrayList<>(console.history);
            Collections.reverse(h);
            for (ConsoleMessage s : h){
                console.forcePrint(s.content,s.level);
            }
        }
        if(!console.isAlive()){
            new Thread(console).start();
        }

        clearConsole();
        Client.getInstance().formatter.getDefaultStream().println(Chalk.on("Vous venez de changer de console.").bgWhite().black());
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

        timer.scheduleAtFixedRate(() -> {
            console.write(console.writing);
            timer.shutdown();
        },250,1,TimeUnit.MILLISECONDS);

        console.iConsole.consoleChange();


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
    public void forcePrint(String s, Level level){
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
                Client.getInstance().formatter.getDefaultStream().print("\033[H\033[2J");
                Client.getInstance().formatter.getDefaultStream().flush();
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

        boolean isWindows = Config.isWindows();
        try {
            String data;

            if(actualConsole == null || !instances.containsKey(actualConsole)){
                actualConsole = defaultConsole;
            }

            while (isRunning && (data =consoleReader.reader.readLine()) != null){
                if(Console.actualConsole.equals(name)){
                try {
                    String[] args = new String[0];

                    args = data.split(" ");

                    iConsole.listener(args);

                    if(!Config.isWindows()){
                        write(writing);
                    }
                }catch (Exception e){
                    fPrint(Chalk.on("ERROR CAUSE>> "+e.getMessage()+" || "+ e.getClass().getSimpleName()).red(),Level.SEVERE);
                    for(StackTraceElement s : e.getStackTrace()){
                        Client.getInstance().formatter.getDefaultStream().println("----->");
                        fPrint("ERROR ON>> "+Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET(),Level.SEVERE);
                    }
                    if(Client.getInstance().isDebug()){
                        e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
                    }else {
                        Client.getInstance().formatter.getDefaultStream().println("Please contact the DN developpers about this error.");
                    }
                }



                }

            }


        }catch (Exception e){

        }
    }
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int readByte = consoleReader.reader.read();
        while (readByte>-1 && readByte!= '\n')
        {
            sb.append((char) readByte);
            readByte = consoleReader.reader.read();
        }
        return sb.length()==0?null:sb.toString();
    }
    private String interruptibleReadLine(BufferedReader reader)
            throws InterruptedException, IOException {
        Pattern line = Pattern.compile("^(.*)\\R");
        Matcher matcher;
        boolean interrupted = false;

        StringBuilder result = new StringBuilder();
        int chr = -1;
        do {
            if (reader.ready()) chr = reader.read();
            if (chr > -1) result.append((char) chr);
            matcher = line.matcher(result.toString());
            interrupted = Thread.interrupted(); // resets flag, call only once
        } while (!interrupted && !matcher.matches());
        if (interrupted) throw new InterruptedException();
        return (matcher.matches() ? matcher.group(1) : "");
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
    public String readLineTimeout(BufferedReader reader, long timeout) throws TimeoutException, IOException {
        long start = System.currentTimeMillis();

        while (!reader.ready()) {
            if (System.currentTimeMillis() - start >= timeout)
                throw new TimeoutException();

            // optional delay between polling
            try { Thread.sleep(50); } catch (Exception ignore) {}
        }

        return reader.readLine(); // won't block since reader is ready
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
                    if(isRunning){
                        Client.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
                        scheduler.shutdown();
                    }

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