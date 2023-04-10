package be.alexandre01.dreamnetwork.core.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class IntroductionConsole {

    protected Console console;
    ScheduledExecutorService executor;

    public IntroductionConsole(String name){
        console = Console.load("m:intro"+name);
        console.setWriting("");

        NodeBuilder yes = new NodeBuilder(NodeBuilder.create("yes"),console);
        NodeBuilder no = new NodeBuilder(NodeBuilder.create("no"),console);

        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public void onKill(LineReader reader) {
                //Shutdown other things
                Console.getConsole("m:default").getKillListener().onKill(reader);
            }
        });


        run();
    }

    public void run(){


        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
               clear();
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(args[0].equalsIgnoreCase("yes")){
                    new IntroHelp();
                    Console.setActualConsole("m:introhelp",true,false);
                }
                if(args[0].equalsIgnoreCase("no")){
                    Console.setActualConsole("m:default");
                }
            }

            @Override
            public void consoleChange() {
                console.setWriting(Console.getFromLang("introduction.ask.firstTime"));

                ConsoleReader.sReader.runMacro("yes");
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                clear();

            }
        });


    }

    private void sendHelp(){
        console.fPrintLang("introduction.help.spiget.helpSpiget");
        console.fPrint("- DOWNLOAD", Level.INFO);
        console.fPrintLang("introduction.help.spiget.search");
        console.fPrint("- SELECT [ID/URL]",Level.INFO);
        console.fPrint("- EXIT",Level.INFO);
    }

    protected void clear(){
        Console.clearConsole();
        ASCIIART.sendTutorial();
    }

}
