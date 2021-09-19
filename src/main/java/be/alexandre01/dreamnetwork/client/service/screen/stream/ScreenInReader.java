package be.alexandre01.dreamnetwork.client.service.screen.stream;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;

import be.alexandre01.dreamnetwork.client.console.ConsoleMessage;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;


import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScreenInReader extends Thread {
    Console console;
    InputStream in;
    JVMService server;
    public InputStream reader;
    private Screen screen;

    public boolean isRunning;
    private StringBuilder datas = new StringBuilder();
    public ScreenInReader(Console console, JVMService server, InputStream reader, Screen screen) {
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

                if(!process.isAlive()){
                    screen.destroy();
                    isRunning = false;
                }


                String data = new String(datas.toString());
                

                if(datas.length() != 0){
                    console.printNL(data);
                    datas.setLength(0);
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
            exception.printStackTrace(Client.getInstance().formatter.getDefaultStream());
            Console.setActualConsole("m:default");
        }
    }

    }

