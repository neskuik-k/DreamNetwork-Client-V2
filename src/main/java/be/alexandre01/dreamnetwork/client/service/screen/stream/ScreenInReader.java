package be.alexandre01.dreamnetwork.client.service.screen.stream;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMService;


import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScreenInReader extends Thread {
    Console console;
    InputStream in;
    JVMService server;
    public BufferedReader reader;
    public boolean isRunning;
    private StringBuilder datas = new StringBuilder();
    public ScreenInReader(Console console, JVMService server,BufferedReader reader) {
        this.console = console;
        this.server = server;
        this.reader = reader;
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
                    service.shutdown();
                    return;
                }

                String data = new String(datas.toString());

                PrintStream p = Client.getInstance().formatter.getDefaultStream();
                if(datas.length() != 0){
                    p.print(data);
                }
                datas.setLength(0);
            }
        },250,250,TimeUnit.MILLISECONDS);
        try {
            CharBuffer buffer = CharBuffer.allocate(1024);

            String data = null;
            int i = 0;
            while (reader.read(buffer) != -1 && this.isRunning) {
                buffer.flip();
                datas.append(buffer);
                buffer.clear();

                //datas.append(i).append(" ").append(data).append("\n");
              //  Client.getInstance().formatter.getDefaultStream().print("HMMM2 "+data+"\n");
            }

        } catch (Exception exception) {
            exception.printStackTrace(Client.getInstance().formatter.getDefaultStream());
            Console.setActualConsole("m:default");
        }
    }



    public void sleep(){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    }

