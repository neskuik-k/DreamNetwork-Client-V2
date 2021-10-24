package be.alexandre01.dreamnetwork.client.commands;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.lists.*;
import be.alexandre01.dreamnetwork.client.console.Console;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandReader{
    CommandsManager commands;
    Console console;
    private boolean stop = false;

    public CommandReader(){
        commands = new CommandsManager();
        commands.addCommands(new ServiceCommand("service"));
        commands.addCommands(new BundlesCommand("bundles"));
        commands.addCommands(new HelpCommand("help"));
        commands.addCommands(new SpigetCommand("spiget"));
        commands.addCommands(new ClearCommand("clear"));
        commands.addCommands(new QuitCommand("quit"));
        commands.addCommands(new EditCommand("edit"));

    }


    public void run(Console console){
        this.console = console;
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    if(args.length != 0){
                        if(args[0].length() != 0){
                            commands.check(args);
                       }
                    }
                }

                @Override
                public void consoleChange() {

                }
            });

            new Thread(console).start();
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
