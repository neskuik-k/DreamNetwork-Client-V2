package be.alexandre01.dreamnetwork.core.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.accessibility.intro.IntroHelp;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.installer.Installer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import be.alexandre01.dreamnetwork.utils.spiget.Ressource;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.widget.TailTipWidgets;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
                console.setWriting(LanguageManager.getMessage("introduction.ask.firstTime"));

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
        console.fPrint(LanguageManager.getMessage("introduction.help.spiget.helpSpiget"),Level.INFO);
        console.fPrint("- DOWNLOAD", Level.INFO);
        console.fPrint(LanguageManager.getMessage("introduction.help.spiget.search"), Level.INFO);
        console.fPrint("- SELECT [ID/URL]",Level.INFO);
        console.fPrint("- EXIT",Level.INFO);
    }

    protected void clear(){
        Console.clearConsole();
        ASCIIART.sendTutorial();
    }

}
