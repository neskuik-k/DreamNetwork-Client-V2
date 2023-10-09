package be.alexandre01.dreamnetwork.api.console;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.console.history.ReaderHistory;
import org.jline.reader.*;
import org.jline.reader.impl.LineReaderImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.function.Supplier;
import java.util.logging.Level;


import static be.alexandre01.dreamnetwork.api.console.Console.*;

public class ConsoleThread extends Thread {


    boolean isRunning = true;

    static boolean isLocked = false;

    static boolean toRestart = false;
    static ConsoleThread instance;

    public ConsoleThread(){
        instance = this;
    }

    public static void resetAndRun(){
        //stopCurrent();
        if(isLocked){
           // Console.debugPrint("Console is locked, waiting for unlock");
            toRestart = true;
            return;
        }
      //  Console.debugPrint("Restarting Console Thread");
        /*Thread thread = new Thread(new ConsoleThread());*/

        if(instance == null){
            new Thread(new ConsoleThread()).start();
            return;
        }
        if(!instance.isRunning) return;
       // System.out.println("IS RUNNING");
        instance.run();
    }


    public static ConsoleThread get(){
        return instance;
    }


    public static void stopCurrent(){
        if(instance != null)
            instance.isRunning = false;
    }

   public static void startCurrent(){
        if(instance != null)
            instance.isRunning = true;
        resetAndRun();
    }
    @Override
    public void run() {
            try {
                String data;

                if(Console.getCurrent() == null || !Console.getInstances().containsKey(actualConsole)){
                    Console.actualConsole = Console.defaultConsole;
                }


                LineReader reader = DNUtils.get().getConsoleManager().getConsoleReader().getSReader();

                //  System.out.println("Console Thread Started");

                PrintWriter out = new PrintWriter(reader.getTerminal().writer());

                while (isRunning){
                    String writing = getConsole(actualConsole).writing;

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
                    isLocked = true;

                   // before readline

                    data = CompletableFuture.supplyAsync(new Supplier<String>() {
                        @Override
                        public String get() {
                            try {
                                return reader.readLine(writing);
                            }catch (UserInterruptException e){
                                SIG_IGN();
                            }catch (EndOfFileException e){
                                SIG_IGN();
                            }catch (Exception e) {
                                bug(e);
                            }
                            return null;
                        }
                    }).get();

                    isLocked = false;

                    if (data == null)
                        continue;


                    final Console console = Console.getConsole(actualConsole);


                    console.sendToLog("> : "+data,Level.INFO);

                    try {
                        if(console.collapseSpace)
                            data = data.trim().replaceAll("\\s{2,}", " ");
                        if(!data.isEmpty() && console.isShowInput())
                            out.println("=> "+ data);
                        out.flush();


                        if(!console.getOverlays().isEmpty()){
                            console.getOverlays().get(0).on(data);
                            continue;
                        }
                        //ConsoleReader.sReader.resetPromptLine(  ConsoleReader.sReader.getPrompt(),  "",  0);
                        ReaderHistory.getLines().put(console.name, data);
                        final String[] args = data.split(" ");


                        console.iConsole.listener(args);
                      /*  CompletableFuture.runAsync(() -> {

                        });*/
                    }catch (Exception e){
                        bug(e);
                    }
                    if(toRestart){
                        toRestart = false;
                    }
                }


            }catch (UserInterruptException e){
                isLocked = false;
                SIG_IGN();
            }
            catch (EndOfFileException e){
                isLocked = false;
                SIG_IGN();
            }
            catch (Exception e){
                isLocked = false;
                debugPrint(getFromLang("console.closed"));
                bug(e);
            }
         resetAndRun();
    }
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();

      /*  while (readByte>-1 && readByte!= '\n')
        {
            sb.append((char) readByte);
            readByte = consoleReader.reader.read();
        }*/
        return IConsoleReader.getReader().readLine();
    }

    public void write(String str){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isRunning){
                        DNUtils.get().getConsoleManager().getFormatter().getDefaultStream().write(stringToBytesASCII(str));
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