package be.alexandre01.dreamnetwork.core.accessibility;

import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.installer.Installer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
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

    Console console;
    ScheduledExecutorService executor;

    public IntroductionConsole(){



        console = Console.load("m:intro");
        console.setWriting("");

        System.out.println(console.writing);
        System.out.println(console);

        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public void onKill(LineReader reader) {
                executor.shutdown();
                Console.setActualConsole("m:default");
                Console nConsole = Console.getConsole("m:default");
                nConsole.run();
            }
        });


        run();
    }

    public void run(){


        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void consoleChange() {
                console.setWriting("It is your first time you use DreamNetwork ? (Y/N) ");
                ConsoleReader.sReader.setTailTip("yes");
                ConsoleReader.sReader.callWidget("complete");
                ConsoleReader.sReader.getHighlighter().highlight(ConsoleReader.sReader,"yes");
                List<AttributedString> mainDesc = new ArrayList<>();
                mainDesc.add(new AttributedString("yes"));
                Map<String, CmdDesc> tailTips = new HashMap<>();
                tailTips.put("widget", new CmdDesc(mainDesc, ArgDesc.doArgNames(Arrays.asList("[pN...]")), null));
                TailTipWidgets tailtipWidgets = new TailTipWidgets(ConsoleReader.sReader, tailTips, 0, TailTipWidgets.TipType.TAIL_TIP);
                tailtipWidgets.enable();
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
