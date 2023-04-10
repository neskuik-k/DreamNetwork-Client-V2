package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import org.jline.reader.LineReader;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatsConsole {
    Console console;
    PrintStream printStream;
    LineReader lineReader;
    ScheduledExecutorService executorService;
    public StatsConsole(Console console){
        this.console = console;
        console.writing = "- ";
        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public void onKill(LineReader reader) {
                Console.setActualConsole("m:default");
                executorService.shutdown();
            }
        });


        run();
    }

    public void run(){

        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {

            }

            @Override
            public void consoleChange() {
                executorService =Executors.newSingleThreadScheduledExecutor();
                lineReader = ConsoleReader.sReader;
                printStream =  Core.getInstance().formatter.getDefaultStream();

                Console.printLang("console.consoleChanged");
                executorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if(Console.actualConsole != "m:stats"){
                            executorService.shutdown();
                        }

                        System.out.println(lineReader.getTerminal().getWidth());
                        Console.clearConsole();
                        int rows = lineReader.getTerminal().getSize().getRows();
                        int cols = lineReader.getTerminal().getSize().getColumns();
                        for (int i = 0; i < rows; i++) {
                            StringBuilder line = new StringBuilder("");
                            if(i == rows/8){
                                for (int j = 0; j < cols/2; j++) {
                                    line.append(" ");
                                }
                                line.append(Console.getFromLang("console.DNStats"));
                            }
                            if(i == rows/6-1){
                                double usage = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(double)(1024 * 1024 * 1024);;

                                DecimalFormat round = new DecimalFormat("###,##0.0");
                                double total = Runtime.getRuntime().maxMemory()/(double)(1024 * 1024*1024);;

                                String s = round.format(usage) +"GiB / "+ round.format(total) +"GiB";
                                for (int j = 0; j < cols/2-s.length(); j++) {
                                    line.append(" ");
                                }

                                printStream.println(s);
                            }
                            if(i == rows/6){
                                long usage = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                                long pourcentage = (usage*100)/Runtime.getRuntime().totalMemory();


                                for (int j = 0; j < cols/3; j++) {
                                    line.append(" ");
                                }
                                line.append(Console.getFromLang("console.stats.memory"));
                                int calcCol = 2*cols/3-(cols/3+10);
                                //kb to mb totalmemory

                                String text = pourcentage+"%";
                                String info = ""+(int)(Runtime.getRuntime().totalMemory()/1024)/1024+" GO";

                                for (int j = 0; j < calcCol; j++) {
                                    char c = '⋱';
                                    if(calcCol >= text.length() && j < text.length()){
                                        c = text.charAt(j);
                                    }
                                    if(j == 0){

                                    }
                                    if(pourcentage < (j*100)/calcCol){
                                        if(c != '⋱'){
                                            line.append(Colors.BLACK_BOLD+Colors.WHITE_BACKGROUND+c);
                                        }else {
                                            line.append(Colors.WHITE + "░");
                                        }
                                    }else {
                                        if(pourcentage < 50){
                                            line.append(Colors.WHITE_BOLD + Colors.GREEN_BACKGROUND).append(c);
                                            continue;
                                        }
                                        if(pourcentage < 75){
                                            line.append(Colors.WHITE_BOLD + Colors.YELLOW_BACKGROUND).append(c);
                                            continue;
                                        }
                                        if(pourcentage < 100){
                                            line.append(Colors.WHITE_BOLD + Colors.RED_BACKGROUND).append(c);
                                            continue;
                                        }
                                    }
                                }

                            }
                            printStream.println(line);
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);

            }
        });
    }

    private void print(String s){
       printStream.print(s);
    }
}
