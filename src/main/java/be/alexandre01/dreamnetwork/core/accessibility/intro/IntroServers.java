package be.alexandre01.dreamnetwork.core.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.accessibility.create.CreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class IntroServers extends IntroductionConsole {

    public IntroServers() {
        super("servers");

        NodeBuilder help = new NodeBuilder(NodeBuilder.create("yes"),console);
    }

    boolean hasCreatedProxy = false;

    @Override
    public void run() {
        console.setConsoleAction(new Console.IConsole() {
            boolean isHelp = false;
            @Override
            public void listener(String[] args) {
               clear();


                if(args[0].equalsIgnoreCase("yes")){
                    if(!hasCreatedProxy){
                        console.setWriting("");
                        Core.getInstance().getCreateTemplateConsole().show("proxies", "proxy", "STATIC", "512M", "1024M", "0", new CreateTemplateConsole.Future() {
                            @Override
                            public void onResponse() {
                                hasCreatedProxy = true;
                                Console.setActualConsole("m:introservers",true);
                            }

                            @Override
                            public void finish() {
                                clear();
                                Console.debugPrint("One more last thing...");
                                console.setWriting(Colors.GREEN+"Do you want help to create a Server ?  : "+Colors.RED);
                                ConsoleReader.sReader.runMacro("yes");

                                //Console.reload();
                                ConsoleReader.sReader.getTerminal().flush();
                                ConsoleReader.sReader.getTerminal().writer().flush();

                            }
                        });
                    }else {
                        Core.getInstance().getCreateTemplateConsole().show("main", "lobby", "STATIC", "1G", "2G", "0", new CreateTemplateConsole.Future() {
                            @Override
                            public void onResponse() {

                            }

                            @Override
                            public void finish() {
                                Console.clearConsole();
                                ASCIIART.sendLogo();
                                ASCIIART.sendTitle();

                                Console.setActualConsole("m:default",true,false);
                            }
                        });
                    }
                }

                if(args[0].equalsIgnoreCase("no")){
                    Console.setActualConsole("m:default");
                }
            }

            @Override
            public void consoleChange() {
                ConsoleReader.sReader.getTerminal().flush();

                ConsoleReader.sReader.getTerminal().writer().flush();
                if(!hasCreatedProxy){
                    console.setWriting(Colors.GREEN+"Do you want help to create a Proxy ?  : "+Colors.RED);
                    ConsoleReader.sReader.runMacro("yes");
                }else {
                   console.setWriting("");
                }
                clear();
            }
        });
    }
}
