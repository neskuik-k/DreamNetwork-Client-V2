package be.alexandre01.dreamnetwork.core.service.screen.stream;



import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import lombok.Getter;


import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ScreenInReader extends Thread implements IScreenInReader {
    Console console;
    InputStream in;
    IService server;
    public InputStream reader;
    @Getter FileHandler fileHandler;

    private Screen screen;

    @Getter private List<ReaderLine> readerLines = new ArrayList<>();
    public boolean isRunning;
    private StringBuilder datas = new StringBuilder();
    public ScreenInReader(Console console, IService server, InputStream reader, Screen screen) {
        this.console = console;
        this.server = server;
        this.reader = reader;

        this.screen = screen;

        if(Main.getGlobalSettings().isLoggingService()){
            try {
                File file = new File(Config.getPath("logs/"+server.getJvmExecutor().getFullName()));
                if(!file.exists()){
                    file.mkdirs();
                }

                // scan all files of folders logs
                File[] files = file.listFiles();
                int id = 1;
                if(files != null){
                    files = Arrays.stream(files).filter(f -> f.getName().endsWith(".log")).toArray(File[]::new);
                    int size = files.length;

                    if(size > 0){
                        File firstFile = files[0];
                        String idSplit = firstFile.getName().split("-")[0];
                       // System.out.println(idSplit);
                        if(idSplit.matches("[0-9]+")){
                            int idSplitInt = Integer.parseInt(idSplit);
                            if(idSplitInt >= id){
                                id = idSplitInt+size;
                            }
                        }
                        int toDelete = size - Main.getGlobalSettings().getLogsByExecutor();
                        if(toDelete > 0){
                            for (int i = 0; i < toDelete; i++) {
                                if(!files[i].delete()){
                                    Console.fine("Can't delete log file "+files[i].getName()+ " because it's used by a process");
                                }
                            }
                        }
                    }


                    for(File target : files){

                        //check if idSplit is multiple numbers
                        /*System.out.println(idSplit);
                        if(idSplit.matches("[0-9]+")){
                            System.out.println("Match");
                            int idSplitInt = Integer.parseInt(idSplit);
                            if(idSplitInt >= id){
                                id = idSplitInt+1;
                            }
                        }*/
                    }
                }

                String idToString = String.valueOf(id);
                if(idToString.length() == 1){
                    idToString = "0"+idToString;
                }

                String name = idToString+"-"+server.getFullName(false);
                if(screen.getService().getUniqueCharactersID().isPresent()){
                    name += "-"+screen.getService().getUniqueCharactersID().get();
                }
                //System.out.println(Config.getPath("logs/"+server.getFullName()+"/"+name+".log"));
                fileHandler = new FileHandler(Config.getPath("logs/"+server.getJvmExecutor().getFullName()+"/"+name+".log"));
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());
            }catch (Exception e){
                System.out.println("Can't create log file");
                // ignore
            }
        }
       /* try {
            int i = reader.read();
            if((i > 500)){
               reader.skip(i-500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void run() {
        Process process = this.server.getProcess();
        in = process.getInputStream();
        InputStream err = process.getErrorStream();
        this.isRunning = true;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
               // System.out.println(process.getOutputStream().getClass());
                //System.out.println(process.getOutputStream().toString());
               if(!process.isAlive()){
                    Console.fine("The PROCESS cannot be read anymore.");
                    //if(screen.getService().getExecutorCallbacks() != null){
                      //  if(screen.getService().getExecutorCallbacks().onStop != null){
                            //screen.getService().getExecutorCallbacks().onStop.whenStop(screen.getService());
                       // }
                    //}
                    screen.destroy(false);
                    isRunning = false;
                }

                if(!isRunning){
                    try {
                        reader.mark(0);
                        reader.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    service.shutdown();
                    return;
                }


                String data = new String(datas.toString());
                

                if(datas.length() != 0){
                    while(data.contains("\n\n") || data.contains("\n\r") ){
                        data = data.replaceAll("\n\n","").replaceAll("\n\r","");
                    }
                    String[] args = data.split("\n");
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < args.length; i++) {
                        String arg = args[i];
                        for (ReaderLine readerLine : readerLines) {
                            arg = readerLine.readLine(arg);
                            if(arg == null){
                                continue;
                            }
                        }
                        sb.append(arg);
                        if(i != args.length-1)
                            sb.append("\n");
                    }
                    data = sb.toString();
                    if(data.toString() != null){
                        console.printNL(data);
                        if(fileHandler != null){
                            fileHandler.publish(new LogRecord(Level.INFO, data));
                        }
                        datas.setLength(0);
                    }else {
                        datas.setLength(0);
                    }




                    for(AServiceClient client : screen.getDevToolsReading()){
                        client.getRequestManager().sendRequest(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE,data);
                    }
                }

            }
        },250,250,TimeUnit.MILLISECONDS);

        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ReadableByteChannel channel = Channels.newChannel(reader);
            String data = null;

            int i = 0;
            while (channel.read(buffer) != -1 && this.isRunning ) {
                ((Buffer)buffer).flip();
                if(buffer.remaining() != 0)
                    datas.append(StandardCharsets.UTF_8.decode(buffer));
                ((Buffer)buffer).clear();
            }

        } catch (Exception exception) {
            exception.printStackTrace(Core.getInstance().formatter.getDefaultStream());
            Console.setActualConsole("m:default");
        }
    }

}

