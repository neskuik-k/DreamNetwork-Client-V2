package be.alexandre01.dreamnetwork.core.service.screen.stream;



import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.console.Console;

import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import lombok.Getter;


import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScreenInReader extends Thread implements IScreenInReader {
    Console console;
    InputStream in;
    IService server;
    public InputStream reader;
    private Screen screen;

    @Getter private List<ReaderLine> readerLines = new ArrayList<>();
    public boolean isRunning;
    private StringBuilder datas = new StringBuilder();
    public ScreenInReader(Console console, IService server, InputStream reader, Screen screen) {
        this.console = console;
        this.server = server;
        this.reader = reader;

        this.screen = screen;
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
                    screen.destroy();
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
                        datas.setLength(0);
                    }else {
                        datas.setLength(0);
                    }




                    for(IClient client : screen.getDevToolsReading()){
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

