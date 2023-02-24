package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


import be.alexandre01.dreamnetwork.core.console.formatter.Formatter;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;
import org.jline.reader.Buffer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
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
    public boolean isDebug = false;
    public static String actualConsole;
    private Thread thread;
    public boolean isRunning = false;
    public boolean collapseSpace = false;
    public String writing = Colors.CYAN_BOLD_BRIGHT+"Dream"+"NetworkV2"+Colors.BLACK_BACKGROUND_BRIGHT+Colors.YELLOW+"@"+Colors.CYAN_UNDERLINED+ Core.getUsername()+Colors.WHITE+" > "+Colors.ANSI_RESET();
    public PrintStream defaultPrint;
    @Setter @Getter private ConsoleKillListener killListener = new ConsoleKillListener() {
        @Override
        public void onKill(LineReader reader) {
            String data;
            while ((data = reader.readLine( Colors.RED_BOLD_BRIGHT+"do you want to exit ? (y or n) > "+Colors.RESET)) != null){
                if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                    System.exit(0);
                }else {
                    Console.debugPrint("Cancelled.");
                    run();
                }


                break;
            }
        }
    };

    public static Console load(String name){
        Console c = new Console(name);
        if(Core.getInstance().formatter != null){
            c.defaultPrint = Core.getInstance().formatter.getDefaultStream();
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
            console.defaultPrint.println(Chalk.on("You have just changed console. ["+console.getName()+"]").bgWhite().black());
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

        }

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


        if(!instances.containsKey("m:default")){
            debugPrint("Debug: "+s);
            return;
        }
        if(level == Level.FINE){
            fine(s);
            return;
        }
        instances.get("m:default").fPrint(s+Colors.ANSI_RESET(),level);

    }

    public static void fine(Object s){

        FileHandler fh = Core.getInstance().getFileHandler();
        if(Core.getInstance().isDebug()){
           print(s,Level.FINE);
            return;
        }
        if(fh != null){
            LogRecord lr = new LogRecord(Level.FINE,s.toString());
           fh.publish(lr);
        }
    }
    public static void print(String s, Level level,String name){

        instances.get(name).fPrint(s + Colors.ANSI_RESET(),level);
    }
    public void forcePrint(String s, Level level){
        if(Console.actualConsole.equals(name))
            ConsoleReader.sReader.printAbove(Core.getInstance().formatter.getDefaultFormatter().format(new LogRecord(level, s.toString()+Colors.RESET)));
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

            final String msgWithoutColorCodes = s.toString().replaceAll("\u001B\\[[;\\d]*m", "");
            Core.getInstance().getFileHandler().publish(new LogRecord(level,msgWithoutColorCodes));
            if(!isDebug && level == Level.FINE)
                return;

            LineReader lineReader = ConsoleReader.sReader;
            int cols = lineReader.getTerminal().getSize().getColumns();
            String msg = Core.getInstance().formatter.getDefaultFormatter().format(new LogRecord(level, (String) s));
            msg = msg.replaceAll("\\s+$", "");

            cols -= msg.replaceAll("\u001B\\[[;\\d]*m", "").length();
            String spaces = "";
            //random number between cols-3 and 1
            //SNOW
            /*if(cols > 6){
                int random = new Random().nextInt(cols-6 + 1 - 1) + 1;

                for (int i = 0; i < cols-random; i++) {
                    spaces += " ";
                }
                spaces += Colors.WHITE+"❆";
            }*/

            ConsoleReader.sReader.printAbove(msg+spaces);

           // ConsoleReader.sReader.setPrompt(writing);
        }




        refreshHistory(s + Colors.ANSI_RESET(),level);
    }
    public static void print(Object s){
        LineReader lineReader = ConsoleReader.sReader;
        int rows = lineReader.getTerminal().getSize().getRows();
        int cols = lineReader.getTerminal().getSize().getColumns();
        String msg = Core.getInstance().formatter.getDefaultFormatter().format(new LogRecord(Level.INFO, s+Colors.ANSI_RESET()));
        msg = msg.replaceAll("\\s+$", "");
        cols -= msg.replaceAll("\u001B\\[[;\\d]*m", "").length();
        String spaces = "";

        //random number between cols-3 and 1
        //SNOW
        /* if(cols > 6){
            int random = new Random().nextInt(cols-6 + 1 - 1) + 1;
            for (int i = 0; i < cols-random; i++) {
                spaces += " ";
            }
            spaces += Colors.WHITE+"❆";
        }*/
        ConsoleReader.sReader.printAbove(msg+spaces);
    }
    public static Logger getLogger(){
        return Logger.getGlobal();
    }
    public static void debugPrint(Object s){
       // stashLine();
        LineReader lineReader = ConsoleReader.sReader;
        int rows = lineReader.getTerminal().getSize().getRows();
        int cols = lineReader.getTerminal().getSize().getColumns();
        String msg = s.toString().replaceAll("\\s+$", "");
        cols -= msg.replaceAll("\u001B\\[[;\\d]*m", "").length();
        String spaces = "";
        //random number between cols-3 and 1
        //SNOW
        /*
        if(cols > 6){
            int random = new Random().nextInt(cols-6 + 1 - 1) + 1;


            for (int i = 0; i < cols-random; i++) {
                spaces += " ";
            }
            spaces += Colors.WHITE+"❆";
        }*/
        lineReader.printAbove(s+spaces);
       //lineReader.printAbove(s.toString());
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
                final PrintStream defaultStream = Core.getInstance().formatter.getDefaultStream();
                defaultStream.print("\033[H\033[2J");
                defaultStream.flush();
            }
        }
        catch (final Exception ignored)
        {

        }
    }
    public static void clearConsole(PrintStream printStream){

        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }
            else
            {
                printStream.print("\033[H\033[2J");
                printStream.flush();
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
        if(Core.getInstance() != null)
            this.isDebug = Core.getInstance().isDebug();
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

            PrintWriter out = new PrintWriter(reader.getTerminal().writer());

            while (isRunning ){

               if((data = reader.readLine(this.writing)) == null)
                    continue;

                try {
                    if(collapseSpace)
                        data = data.trim().replaceAll("\\s{2,}", " ");
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
                        Core.getInstance().formatter.getDefaultStream().println("----->");
                        fPrint("ERROR ON>> "+Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET(),Level.SEVERE);
                    }
                    if(Core.getInstance().isDebug()){
                        e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                    }else {
                        Core.getInstance().formatter.getDefaultStream().println("Please contact the DN developpers about this error.");
                    }
                    }
                }



        }catch (UserInterruptException e){
            SIG_ING();
        }
        catch (EndOfFileException e){
            SIG_ING();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }
    public void SIG_ING(){
        LineReader reader =  ConsoleReader.sReader;

        //  reader.setPrompt( Colors.YELLOW+"enter the secret-code > "+Colors.RESET);
        PrintWriter out = new PrintWriter(reader.getTerminal().writer());

        try {
            Console.getConsole(actualConsole).killListener.onKill(reader);
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
        if(lvl.equals(Level.FINE) && !Core.getInstance().isDebug()){
            return;
        }
        if(historySize >= 2000){
            history.remove(0);
            historySize--;
        }
        //debugPrint("history >> "+data);

        history.add(new ConsoleMessage(data,lvl));
        historySize += data.length();
    }
    public void refreshHistory(String data){
        if(historySize >= 2000){
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
                        Core.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
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

    public interface ConsoleKillListener {
        void onKill(LineReader reader);
    }

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