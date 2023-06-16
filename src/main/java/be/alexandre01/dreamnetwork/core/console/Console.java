package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


import be.alexandre01.dreamnetwork.core.console.history.ReaderHistory;
import be.alexandre01.dreamnetwork.core.console.language.Emoji;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaIndex;
import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.*;

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
    @Getter @Setter private boolean showInput;

    public static Console MAIN;
    private final ArrayList<Overlay> overlays = new ArrayList<>();

    public interface IConsole{
        public void listener(String[] args);
        public void consoleChange();
    }

    public static String getEmoji(String emoji,String ifNot,String... args){
        return Main.getLanguageManager().getEmojiManager().getEmoji(emoji,ifNot,args);
    }
    public static String getEmoji(String emoji, String ifNot){
        return Main.getLanguageManager().getEmojiManager().getEmoji(emoji,ifNot);
    }
    public static String getEmoji(String emoji){
        return getEmoji(emoji,"");
    }
    public void addOverlay(Overlay overlay,String writing){
        if(writing != null){
            this.writing = writing;
        }
        overlay.console = this;
        overlays.add(overlay);
    }

    public void addOverlay(Overlay overlay){
        addOverlay(overlay,null);
    }

    public abstract static class Overlay{
        private Console console;
        public abstract void on(String data);

        public void disable(){
            console.overlays.remove(this);
        }
    }

    private StringBuilder datas = new StringBuilder();


    public List<Completers.TreeCompleter.Node> completorNodes = new ArrayList<>();
    IConsole iConsole;
    private static final HashMap<String, Console> instances = new HashMap<>();
    private static ConsoleReader consoleReader = new ConsoleReader();
    public String name;

    @Getter @Setter private boolean noHistory = false;

    @Getter private static boolean blockConsole = false;

    public static void setBlockConsole(boolean blockConsole) {
        if(blockConsole == Console.blockConsole) return;

        Console.blockConsole = blockConsole;
        Thread thread = Console.getConsole("m:default");
        if(blockConsole){
            if(thread != null){
                //thread.interrupt();
                Console.getConsole("m:default").isRunning = false;// tell the thread to stop
                /*try {
                    thread.join(); // wait for the thread to stop
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }
        }else {
            Console.getConsole("m:default").isRunning = true;
            Console.getConsole("m:default").run();
        }
        //reload();
    }

    private final ArrayList<ConsoleMessage> history;
    private int historySize;
    public static String defaultConsole;
    public boolean isDebug = false;
    public static String actualConsole;
    private Thread thread;
    public boolean isRunning = false;
    public boolean collapseSpace = false;

    public Character readLineChar = null;
    public String readLineString1 = null;
    public String readLineString2 = null;
    public MaskingCallback maskingCallback = null;
    @Setter public String writing = Console.getFromLang("console.dreamnetworkWriting",Core.getUsername());
    public PrintStream defaultPrint;
    @Setter @Getter private ConsoleKillListener killListener = new ConsoleKillListener() {
        @Override
        public boolean onKill(LineReader reader) {
            String data;
            ConsoleReader.getDefaultHighlighter().setEnabled(false);
            while ((data = reader.readLine(Console.getFromLang("console.askExit"))) != null){
                if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                    System.exit(0);
                    return false;
                }else {
                    Console.debugPrint(Console.getFromLang("cancelled"));
                    ConsoleReader.getDefaultHighlighter().setEnabled(true);
                    return true;
                }
            }
            return false;
        }
    };

    public static Console load(String name){
        Console.fine("Loading console "+name);
        Console c = new Console(name);
        if(Core.getInstance().formatter != null){
            c.defaultPrint = Core.getInstance().formatter.getDefaultStream();
        }
        instances.put(name,c);
        return c;
    }
    public static void setActualConsole(String name ,boolean isSilent, boolean clearConsole){
        Console console = instances.get(name);

        ConsoleReader.sReader.setPrompt(console.writing);

        Console.actualConsole = name;
        console.isRunning = true;
        if(clearConsole)
            clearConsole();
        if(console.defaultPrint != null && !isSilent)
            console.defaultPrint.println(Console.getFromLang("console.changed", console.getName()));
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
        try {
            ConsoleReader.sReader.getHistory().purge();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!console.noHistory){
            if(ReaderHistory.getLines().containsKey(name)){
                List<String> h = new ArrayList<>(ReaderHistory.getLines().get(name));
                for (String s : h){
                    ConsoleReader.sReader.getHistory().add(s);
                }

            }
        }
        if(actualConsole == name){
            ConsoleReader.getDefaultHighlighter().setEnabled(true);
        }else{
            ConsoleReader.getDefaultHighlighter().setEnabled(false);
        }

        console.iConsole.consoleChange();
        console.reloadCompletors();
    }

    public static void setActualConsole(String name){
        setActualConsole(name,false,true);
    }
    public static void setActualConsole(String name,boolean isSilent){
        setActualConsole(name,isSilent,true);
    }

    public void reloadCompletors(){
        if(completorNodes.isEmpty()){
            completorNodes.add(Completers.TreeCompleter.node(""));
        }
        ConsoleReader.nodes = completorNodes;
        ConsoleReader.reloadCompleter();
    }
    public static void setDefaultConsole(String defaultConsole) {
        if(Console.defaultConsole != null)
            instances.get(defaultConsole).stop();
        Console.defaultConsole = defaultConsole;
    }





    public static Console getConsole(String name) {
        return instances.get(name);
    }

    public static Console getCurrent(){
        return instances.get(actualConsole);
    }

    public static Collection<Console> getConsoles(){
        return instances.values();
    }

    public static void print(Object s, Level level){
        LineReader consoleReader  = ConsoleReader.sReader;


        if(!instances.containsKey("m:default")){
            debugPrint("Debug: "+s);
            return;
        }
        if(level == Level.FINE){
            FileHandler fh = Core.getInstance().getFileHandler();
            String msg = s == null ? "null": s.toString();
            if(Core.getInstance().isDebug()){
                instances.get(actualConsole).fPrint(s+Colors.ANSI_RESET(),level);

               // print(msg,Level.FINE);
                return;
            }
            if(fh != null){
                sendToLog(msg,Level.FINE,"global");
            }
            return;
        }

        instances.get(actualConsole).fPrint(s + Colors.ANSI_RESET(),level);
        //instances.get("m:default").fPrint(s+Colors.ANSI_RESET(),level);

    }

    public static void fine(Object s){
        print(s,Level.FINE);
    }
    public static void fineLang(String map, Object s){
        printLang(map,s,Level.FINE);
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

    public void fPrintLang(String map, Level level, Object... params){
        if (Core.getInstance().isDebug()){
            fPrint(LanguageManager.getMessage(map,params) + " ["+map+"]", level);
            return;
        }
        fPrint(LanguageManager.getMessage(map,params),level);
    }
    public void fPrintLang(String map, Object... params){
        fPrintLang(map,Level.INFO,params);
    }
    public void fPrint(Object s,Level level){
        //stashLine();

        if(Console.actualConsole.equals(name)){

            //Client.getLogger().log(level,s+Colors.ANSI_RESET());


            if(!isDebug && level == Level.FINE)
                return;

            LineReader lineReader = ConsoleReader.sReader;
            int cols = lineReader.getTerminal().getSize().getColumns();
            String msg = Core.getInstance().formatter.getDefaultFormatter().format(new LogRecord(level, (String) s));
            msg = msg.replaceAll("\\s+$", "");


            ConsoleReader.sReader.printAbove(msg);

           // ConsoleReader.sReader.setPrompt(writing);
        }
        sendToLog(s,level);
        refreshHistory(s + Colors.ANSI_RESET(),level);
    }

    public static void printLang(String map,Level level,Object... params){
        if(Core.getInstance().isDebug()) {
            print(LanguageManager.getMessage(map,params) + " ["+map+"]", level);
            return;
        }
        print(LanguageManager.getMessage(map,params),level);
    }

    public static void printLang(String map,Object... params){
        printLang(map,Level.INFO,params);
    }

    public static String getFromLang(String map,Object... params){
        return LanguageManager.getMessage(map,params);
    }
    public static void print(Object s){
        LineReader lineReader = ConsoleReader.sReader;
        String msg = Core.getInstance().formatter.getDefaultFormatter().format(new LogRecord(Level.INFO, s+Colors.ANSI_RESET()));
        msg = msg.replaceAll("\\s+$", "");
        sendToLog(s,Level.INFO,"global");
        ConsoleReader.sReader.printAbove(msg);
    }

    private void sendToLog(Object s,Level level){
        final String msgWithoutColorCodes = s.toString().replaceAll("\u001B\\[[;\\d]*m", "");
        Core.getInstance().getFileHandler().publish(new LogRecord(level, msgWithoutColorCodes + "| @" + name));
    }
    private static void sendToLog(Object s,Level level,String name){
        final String msgWithoutColorCodes = s.toString().replaceAll("\u001B\\[[;\\d]*m", "");
        Core.getInstance().getFileHandler().publish(new LogRecord(level, msgWithoutColorCodes + "| @" + name));
    }
    public static Logger getLogger(){
        return Logger.getGlobal();
    }
    public static void debugPrint(Object s){
       // stashLine();
        LineReader lineReader = ConsoleReader.sReader;
        if(s == null)
            s = "null";
        String msg = s.toString().replaceAll("\\s+$", "");

        lineReader.printAbove(s.toString());
       //lineReader.printAbove(s.toString());
       // Client.getInstance().formatter.getDefaultStream().println(s+Colors.ANSI_RESET());
        //unstashLine();
    }


    public static void clearConsole(){
        clearConsole(Core.getInstance().formatter.getDefaultStream());
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
       /* if(thread == null){
            thread = new Thread(this);
           // thread.start();
        }*/

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

    public static void reload(){

        Thread thread = Console.getConsole("m:default");
        if(thread != null){
            thread.interrupt();
            Console.getConsole("m:default").isRunning = false;// tell the thread to stop
            try {
                thread.join(); // wait for the thread to stop
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Console.getConsole("m:default").isRunning = true;
            thread.start();
        }
    }

    public static void bug(Exception e){
        if(Console.actualConsole == null){
            e.printStackTrace();
            return;
        }
        Console.printLang("console.errorOn", Level.WARNING, e.getMessage(), e.getClass().getSimpleName());
        Console console = Console.getConsole(actualConsole);
        console.fPrintLang("console.errorCause", Level.SEVERE, e.getLocalizedMessage(), e.getClass().getSimpleName());
        for(StackTraceElement s : e.getStackTrace()){
            //Core.getInstance().formatter.getDefaultStream().println("----->");
            console.fPrintLang("console.errorOn", Level.SEVERE, s.getClassName(), s.getMethodName()+" on "+s.getFileName(), s.getLineNumber());
        }
        if(Core.getInstance().isDebug()){
            e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
        }else {
            Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("console.contactDNDevError"));
            Core.getInstance().getFileHandler().publish(new LogRecord(Level.SEVERE,Console.getFromLang("console.contactDNDevError")));
        }
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

            while (isRunning){
                Console console = Console.getConsole(actualConsole);
            //    System.out.println(blockConsole);

              /* if(blockConsole){
                    continue;
                }else {
                    Core.getInstance().getFileHandler().publish(new LogRecord(Level.INFO, "Console Blocked: "+blockConsole+""));
                }

               // System.out.println("Line Refresh");
                if(true){
                    continue;
                }*/
               if((data = reader.readLine(console.writing, readLineString1,maskingCallback,readLineString2)) == null)
                    continue;

                console = Console.getConsole(actualConsole);


                sendToLog("> : "+data,Level.INFO);

                try {
                    if(console.collapseSpace)
                        data = data.trim().replaceAll("\\s{2,}", " ");
                    if(data.length() != 0 && console.showInput)
                        out.println("=> "+ data);
                    out.flush();


                    if(overlays.size() > 0){
                        overlays.get(0).on(data);
                        continue;
                    }
                    //ConsoleReader.sReader.resetPromptLine(  ConsoleReader.sReader.getPrompt(),  "",  0);
                    ReaderHistory.getLines().put(console.name, data);
                    String[] args = new String[0];
                    args = data.split(" ");
                    console.iConsole.listener(args);
                }catch (Exception e){
                    bug(e);
                }
            }


        }catch (UserInterruptException e){
            SIG_IGN();
        }
        catch (EndOfFileException e){
            SIG_IGN();
        }
        catch (Exception e){
            Console.debugPrint(Console.getFromLang("console.closed"));
            e.printStackTrace();
        }
    }
    public void SIG_IGN(){
        if(!Main.getGlobalSettings().isSIG_IGN_Handler()){
            Console.debugPrint(Console.getFromLang("console.closed"));
            System.exit(0);
        }
        LineReader reader =  ConsoleReader.sReader;

        //  reader.setPrompt( Colors.YELLOW+"enter the secret-code > "+Colors.RESET);
        // PrintWriter out = new PrintWriter(reader.getTerminal().writer());

        try {
            if(Console.getConsole(actualConsole).killListener.onKill(reader))
                Console.getConsole(actualConsole).run();
        }catch (UserInterruptException e){
            SIG_IGN();
        }
        catch (EndOfFileException e){
            SIG_IGN();
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
       // debugPrint("history >> "+data);
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
        boolean onKill(LineReader reader);
    }
}