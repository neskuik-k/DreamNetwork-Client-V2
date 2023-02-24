package be.alexandre01.dreamnetwork.core.utils;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;

public class ASCIIART {
    public static void sendLogo(){
        Chalk.setColorEnabled(true);
        Console.debugPrint(Chalk.on(Colors.CYAN+
                         "                                  ./((((/.            \n" +
                        "                                (///////////          \n" +
                        "                       ,(((((*///////////////.        \n" +
                        "                       /(((((////////////////,        \n" +
                        "                   (((((((((////////"+ Colors.CYAN_BOLD_BRIGHT+"/////////////,    \n" +
                        "                 /(((((((((////////////////////////   \n" +
                        "                 /((((((((/////////////////////////   \n" +
                        "                  ,((((((/////////////////////////    \n"+
                        "                         ````````````````  ").bold());
    }

    public static void sendTitle(){
        Console.debugPrint(Colors.CYAN_BRIGHT+
                "______                               _   _        _                          _    \n" +
                        "|  _  \\                             | \\ | |      | |                        | |   \n" +
                        "| | | | _ __  ___   __ _  _ __ ___  |  \\| |  ___ | |_ __      __ ___   _ __ | | __\n" +
                        "| | | || '__|/ _ \\ / _` || '_ ` _ \\ | . ` | / _ \\| __|\\ \\ /\\ / // _ \\ | '__|| |/ /\n" +
                        "| |/ / | |  |  __/| (_| || | | | | || |\\  ||  __/| |_  \\ V"+Colors.PURPLE_BOLD_BRIGHT+"  V /| (_) || |   |   <     "+Colors.WHITE_BOLD_UNDERLINED+"2023"+Colors.RESET+Colors.PURPLE_BOLD_BRIGHT+"\n" +
                        "|___/  |_|   \\___| \\__,_||_| |_| |_|\\_| \\_/ \\___| \\__|  \\_/\\_/  \\___/ |_|   |_|\\_\\      "+Colors.YELLOW_BOLD_BRIGHT_UNDERLINE+"(BETA V2 - 1.11)\n" +Colors.RESET);
    }

}
