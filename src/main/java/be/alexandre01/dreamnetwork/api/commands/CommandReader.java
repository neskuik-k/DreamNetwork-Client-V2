package be.alexandre01.dreamnetwork.api.commands;



import be.alexandre01.dreamnetwork.api.events.list.commands.CoreCommandExecuteEvent;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.commands.lists.*;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static be.alexandre01.dreamnetwork.core.console.Console.print;
import static org.jline.builtins.Completers.TreeCompleter.node;

public class CommandReader{
    @Getter CommandsManager commands;

    Core core;
    @Getter Console console;
    private boolean stop = false;



    public CommandReader(){
        commands = new CommandsManager();


        core = Core.getInstance();

        ConsoleReader.nodes.add(node("test"));


    }

    public void init(){
        commands.addCommands(new ServiceCommand("service"));
        commands.addCommands(new BundlesCommand("bundle"));
        commands.addCommands(new HelpCommand("help"));
        commands.addCommands(new SpigetCommand("spiget"));
        commands.addCommands(new ClearCommand("clear"));
        commands.addCommands(new QuitCommand("quit"));
        commands.addCommands(new EditCommand("edit"));
        commands.addCommands(new GuiCommand("gui"));
        commands.addCommands(new HypervisorCommand("hypervisor"));
        ConsoleReader.reloadCompleter();
    }

    public void run(Console console){
        this.console = console;
        console.collapseSpace = true;
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    if(args.length != 0){
                        if(args[0].length() != 0){
                            CoreCommandExecuteEvent event = new CoreCommandExecuteEvent(core.getDnCoreAPI(), args);
                            core.getEventsFactory().callEvent(event);
                            if(event.isCancelled()){
                                print("Command cancelled");
                                return;
                            }
                            commands.check(args);
                       }
                    }
                }

                @Override
                public void consoleChange() {
                    //DO NOTHING
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
                    Core.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
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
