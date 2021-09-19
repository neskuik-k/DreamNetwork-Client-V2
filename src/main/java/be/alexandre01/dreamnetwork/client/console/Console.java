package be.alexandre01.dreamnetwork.client.console;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;


import com.github.tomaslanger.chalk.Chalk;
import org.jline.reader.Buffer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console extends Thread{
    public interface IConsole{
        public void listener(String[] args);
        public void consoleChange();
    }

    private StringBuilder datas = new StringBuilder();
    IConsole iConsole;
    private static final HashMap<String, Console> instances = new HashMap<>();
    private static ConsoleReader consoleReader = new ConsoleReader();
    public String name;
    private final ArrayList<ConsoleMessage> history;
    private int historySize;
    public static String defaultConsole;
    public static String actualConsole;
    private Thread thread;
    public boolean isRunning = false;
    public String writing = Colors.CYAN+"Dream"+"NetworkV2"+Colors.BLACK_BACKGROUND_BRIGHT+Colors.YELLOW+"@"+Colors.CYAN+Client.getUsername()+Colors.WHITE+" > "+Colors.ANSI_RESET();
    ScheduledExecutorService scheduler = null;
    public PrintStream defaultPrint;

    public static Console load(String name){
        Console c = new Console(name);
        if(Client.getInstance() != null){
            c.defaultPrint = Client.getInstance().formatter.getDefaultStream();
        }
        instances.put(name,c);
        return c;
    }
    public static void setActualConsole(String name){
        Console console = instances.get(name);
        Console.actualConsole = name;
        console.isRunning = true;
        clearConsole();
        if(console.defaultPrint != null)
            console.defaultPrint.println(Chalk.on("Vous venez de changer de console. ["+console.getName()+"]").bgWhite().black());
        if(!console.history.isEmpty()){
            List<ConsoleMessage> h = new ArrayList<>(console.history);
          //  stashLine();
          for (ConsoleMessage s : h){
                if(s.level == null){
                    console.defaultPrint.print(s.content);
                }else{
                    console.forcePrint(s.content,s.level);
                }
            }
            //unstashLine();
        }
        if(!console.isAlive()){
            //new Thread(console).start();
        }


        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    /*    timer.scheduleAtFixedRate(() -> {
            console.write(console.writing);
            timer.shutdown();
        },250,1,TimeUnit.MILLISECONDS);*/

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
        LineReader consoleReader  = ConsoleReader.sReader;

        if(consoleReader.getHistory() == null || consoleReader.getHistory().size() == 0){
            return;
        }



        instances.get("m:default").fPrint(s+Colors.ANSI_RESET(),level);

    }
    public static void print(String s, Level level,String name){
        instances.get(name).fPrint(s + Colors.ANSI_RESET(),level);
    }
    public void forcePrint(String s, Level level){
        if(Console.actualConsole.equals(name))
            ConsoleReader.sReader.printAbove(Client.getInstance().formatter.getDefaultFormatter().format(new LogRecord(level, s.toString()+Colors.RESET)));
    }
    /*
    Print without log
     */
    public void printNL(Object s){
        if(Console.actualConsole.equals(name)){
          //  stashLine();
            ConsoleReader.sReader.printAbove(s.toString());
           // defaultPrint.print(s);

          //  ConsoleReader.sReader.setPrompt(writing);
           // unstashLine();
        }
        refreshHistory(s + "");
    }

    /*
    Basic Print in console
     */
    public void fPrint(Object s,Level level){
        //stashLine();
        if(Console.actualConsole.equals(name)){

            //Client.getLogger().log(level,s+Colors.ANSI_RESET());
            ConsoleReader.sReader.printAbove(Client.getInstance().formatter.getDefaultFormatter().format(new LogRecord(level, (String) s)));
           // ConsoleReader.sReader.setPrompt(writing);
        }
        if(scheduler == null){
            //taskUnstash();
        }



        refreshHistory(s + Colors.ANSI_RESET(),level);
    }
    public static void print(Object s){
        ConsoleReader.sReader.printAbove(Client.getInstance().formatter.getDefaultFormatter().format(new LogRecord(Level.INFO, s+Colors.ANSI_RESET())));
    }

    public static void debugPrint(Object s){
       // stashLine();
        ConsoleReader.sReader.printAbove(s.toString());
       // Client.getInstance().formatter.getDefaultStream().println(s+Colors.ANSI_RESET());
        //unstashLine();
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
        if(thread == null){
            thread = new Thread(this);
           // thread.start();
        }

    }
    public IConsole getConsoleAction(){
        return iConsole;
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

            LineReader reader =  ConsoleReader.sReader;

            //reader.setPrompt(  this.writing);
            PrintWriter out = new PrintWriter(reader.getTerminal().writer());

            while (isRunning && (data = reader.readLine(this.writing)) != null){

                try {
                    if(data.length() != 0)
                        out.println("=> "+ data);
                    out.flush();

                    //ConsoleReader.sReader.resetPromptLine(  ConsoleReader.sReader.getPrompt(),  "",  0);
                    String[] args = new String[0];
                    args = data.split(" ");
                    Console.getConsole(actualConsole).iConsole.listener(args);
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



        }catch (Exception e){
            SIG_ING();
        }



    }
    public void SIG_ING(){
        LineReader reader =  ConsoleReader.sReader;



        //  reader.setPrompt( Colors.YELLOW+"enter the secret-code > "+Colors.RESET);
        PrintWriter out = new PrintWriter(reader.getTerminal().writer());
        String data;
        try {
            while ((data = reader.readLine( Colors.PURPLE+"do you want to exit ? (y or n) > "+Colors.RESET)) != null){

                    if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                        System.exit(0);
                    }else {
                        Console.debugPrint("Cancelled.");
                        run();
                    }


                    break;

            }
        }catch (UserInterruptException e){
            SIG_ING();
        }
        catch (EndOfFileException e){
            SIG_ING();
        }

    }
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();

      /*  while (readByte>-1 && readByte!= '\n')
        {
            sb.append((char) readByte);
            readByte = consoleReader.reader.read();
        }*/
        return ConsoleReader.sReader.readLine();
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
        if(historySize >= 1000){
            history.remove(0);
            historySize--;
        }
        //debugPrint("history >> "+data);
        history.add(new ConsoleMessage(data,lvl));
        historySize += data.length();
    }
    public void refreshHistory(String data){
        if(historySize >= 1000){
            history.remove(0);
            historySize--;
        }
        //debugPrint("history >> "+data);
        history.add(new ConsoleMessage(data));
        historySize += data.length();
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


    private static Buffer stashed;

    /*  public static void stashLine() {
        LineReader console = ConsoleReader.sReader;
        stashed = console.getBuffer().copy();
        try {
            console.getOutput().write("\u001b[1G\u001b[K");
            console.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public static void unstashLine() {
        try {
           LineReader console = ConsoleReader.sReader;
            console.resetPromptLine(console.getPrompt(),
                    stashed.toString(), stashed.cursor);
        } catch (IOException e) {
            // ignore
        }
    }
    public void taskUnstash(){
        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                unstashLine();
                scheduler.shutdown();
                scheduler = null;
            }
        },50,50, TimeUnit.MILLISECONDS);

    }*/
}