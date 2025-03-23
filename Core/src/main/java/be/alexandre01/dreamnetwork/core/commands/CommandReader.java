package be.alexandre01.dreamnetwork.core.commands;



import be.alexandre01.dreamnetwork.api.commands.ICommandReader;
import be.alexandre01.dreamnetwork.api.commands.ICommandsManager;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.ConsoleThread;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.api.events.list.commands.CoreCommandExecuteEvent;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.commands.lists.*;

import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static be.alexandre01.dreamnetwork.api.console.Console.printLang;


public class CommandReader implements ICommandReader {
    @Getter
    ICommandsManager commands;

    Core core;
    @Getter
    Console console;



    public CommandReader(){
        commands = new CommandsManager();
        core = Core.getInstance();
    }

    public void init(){
        commands.addCommands(new ServiceCommand("service"));
        commands.addCommands(new BundlesCommand("bundle"));
        commands.addCommands(new HelpCommand("help"));
      //  commands.addCommands(new SpigetCommand("spiget"));
        commands.addCommands(new ClearCommand("clear"));
        commands.addCommands(new QuitCommand("quit"));
        commands.addCommands(new EditCommand("edit"));
        commands.addCommands(new GuiCommand("gui"));
        commands.addCommands(new TaskCommand("task"));
        commands.addCommands(new HypervisorCommand("hypervisor"));
        commands.addCommands(new AddonCommand("addon"));
        commands.addCommands(new DeployCommand("deploy"));
/*
        commands.addCommands(new UserCommand("user"));
*/
        IConsoleReader.reloadCompleters();
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
                                printLang("api.commands.cancelled");
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
    }


    @Override
    public void write(String str){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Core.getInstance().formatter.getDefaultStream().write(ICommandReader.stringToBytesASCII(str));
                    scheduler.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },50,50, TimeUnit.MILLISECONDS);

    }
}
