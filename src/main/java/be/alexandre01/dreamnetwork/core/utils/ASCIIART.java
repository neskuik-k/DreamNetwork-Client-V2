package be.alexandre01.dreamnetwork.core.utils;

import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;

public class ASCIIART {
    public static void sendLogo(){
        Chalk.setColorEnabled(true);
        Console.debugPrint(Chalk.on(
                         "                                  ./((((/.            \n" +
                        "                                (///////////          \n" +
                        "                       ,(((((*///////////////.        \n" +
                        "                       /(((((////////////////,        \n" +
                        "                   (((((((((/////////////////////,    \n" +
                        "                 /(((((((((////////////////////////   \n" +
                        "                 /((((((((/////////////////////////   \n" +
                        "                  ,((((((/////////////////////////    \n"+
                        "                         ````````````````  ").cyan());
    }

    public static void sendTitle(){
        Console.debugPrint(Chalk.on(
                        "______                               _   _        _                          _    \n" +
                        "|  _  \\                             | \\ | |      | |                        | |   \n" +
                        "| | | | _ __  ___   __ _  _ __ ___  |  \\| |  ___ | |_ __      __ ___   _ __ | | __\n" +
                        "| | | || '__|/ _ \\ / _` || '_ ` _ \\ | . ` | / _ \\| __|\\ \\ /\\ / // _ \\ | '__|| |/ /\n" +
                        "| |/ / | |  |  __/| (_| || | | | | || |\\  ||  __/| |_  \\ V  V /| (_) || |   |   < \n" +
                        "|___/  |_|   \\___| \\__,_||_| |_| |_|\\_| \\_/ \\___| \\__|  \\_/\\_/  \\___/ |_|   |_|\\_\\\n" +
                        "                                                                                  \n" +
                        "     " +
                        "                                                                             ").blue());
    }
}
