package be.alexandre01.dreamnetwork.client.commands;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Config;
import be.alexandre01.dreamnetwork.client.commands.lists.Add;
import be.alexandre01.dreamnetwork.client.commands.lists.Help;
import be.alexandre01.dreamnetwork.client.console.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandReader{
    Commands commands;
    Console console;
    private boolean stop = false;

    public CommandReader(Console console){
        this.console = console;
        commands = new Commands();
        commands.addCommands(new Help());
        commands.addCommands(new Add());
        run();
    }


    public void run(){
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    if(args.length != 0){
                        if(!args[0].equalsIgnoreCase(" ")){
                            commands.check(args);
                        }
                    }
                }
            });

        if(!Config.isWindows()){
            write("> ");
        }


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
