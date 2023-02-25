package be.alexandre01.dreamnetwork.core.accessibility.add;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.accessibility.intro.IntroHelp;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class CreateTemplateConsole {

    protected Console console;
    ScheduledExecutorService executor;

    public CreateTemplateConsole(String name){
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
                Console.clearConsole();
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(args[0].equalsIgnoreCase("yes")){
                    new IntroHelp();
                    Console.setActualConsole("m:introhelp");
                }
                if(args[0].equalsIgnoreCase("no")){
                    Console.setActualConsole("m:default");
                    Console nConsole = Console.getConsole("m:default");
                    nConsole.run();
                }
            }

            @Override
            public void consoleChange() {
                console.setWriting(Colors.GREEN+"It is your first time you use "+Colors.CYAN+"DreamNetwork"+Colors.GREEN+" ?" +Colors.WHITE+" Type yes or no: "+Colors.RED);

                ConsoleReader.sReader.runMacro("yes");
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });


    }

    private void sendHelp(){
        console.fPrint("HELP SPIGET:",Level.INFO);
        console.fPrint("- DOWNLOAD", Level.INFO);
        console.fPrint("- SEARCH [VALUE] [NAME/TAG/AUTHORS] [PAGE]", Level.INFO);
        console.fPrint("- SELECT [ID/URL]",Level.INFO);
        console.fPrint("- EXIT",Level.INFO);
    }



}
