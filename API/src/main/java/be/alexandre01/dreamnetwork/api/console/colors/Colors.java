package be.alexandre01.dreamnetwork.api.console.colors;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.DNUtils;
import lombok.Setter;

public class Colors {
    @Setter static ColorsData colorsData;

    static {
        System.out.println("Loaded");
        colorsData = DNUtils.get().colorsData;
    }

    public static final String ANSI_RESET = colorsData != null ? colorsData.ANSI_RESET : "\u001B[0m";
    public static final String ANSI_BLACK = colorsData != null ? colorsData.ANSI_BLACK : "\u001B[30m";
    public static final String ANSI_RED =  colorsData != null ? colorsData.ANSI_RED : "\u001B[31m";
    public static final String ANSI_GREEN =  colorsData != null ? colorsData.ANSI_GREEN :"\033[0;32m";
    public static final String ANSI_YELLOW =  colorsData != null ? colorsData.ANSI_YELLOW :"\u001B[33m";
    public static final String ANSI_BLUE =  colorsData != null ? colorsData.ANSI_BLUE :"\u001B[34m";
    public static final String ANSI_PURPLE =  colorsData != null ? colorsData.ANSI_PURPLE :"\u001B[35m";
    public static final String ANSI_CYAN =  colorsData != null ? colorsData.ANSI_CYAN :"\u001B[36m";
    public static final String ANSI_WHITE =  colorsData != null ? colorsData.ANSI_WHITE :"\u001B[37m";
    public static final String ANSI_PWHITE = colorsData != null ? colorsData.ANSI_PWHITE : ANSI_RESET+"\u001B[2m";
    public static final String RESET =  colorsData != null ? colorsData.RESET :"\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK =  colorsData != null ? colorsData.BLACK :"\033[0;30m";   // BLACK
    public static final String RED =  colorsData != null ? colorsData.RED :"\033[0;31m";     // RED
    public static final String GREEN =  colorsData != null ? colorsData.GREEN :"\033[0;32m";   // GREEN
    public static final String YELLOW =  colorsData != null ? colorsData.YELLOW :"\033[0;33m";  // YELLOW
    public static final String BLUE =  colorsData != null ? colorsData.BLUE :"\033[0;34m";    // BLUE
    public static final String PURPLE =  colorsData != null ? colorsData.PURPLE :"\033[0;35m";  // PURPLE
    public static final String CYAN =  colorsData != null ? colorsData.CYAN :"\033[0;36m";    // CYAN
    public static final String WHITE = colorsData != null ? colorsData.WHITE : "\033[0;37m";   // WHITE


    // Bold
    public static final String BLACK_BOLD =  colorsData != null ? colorsData.BLACK_BOLD :"\033[1;30m";  // BLACK
    public static final String RED_BOLD =  colorsData != null ? colorsData.RED_BOLD :"\033[1;31m";    // RED
    public static final String GREEN_BOLD =  colorsData != null ? colorsData.GREEN_BOLD :"\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD =  colorsData != null ? colorsData.YELLOW_BOLD :"\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD =  colorsData != null ? colorsData.BLUE_BOLD :"\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD =  colorsData != null ? colorsData.PURPLE_BOLD :"\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD =  colorsData != null ? colorsData.CYAN_BOLD :"\033[1;36m";   // CYAN
    public static final String WHITE_BOLD =  colorsData != null ? colorsData.WHITE_BOLD :"\033[1;37m";  // WHITE
    public static final String WHITE_BOLD_UNDERLINED =  colorsData != null ? colorsData.WHITE_BOLD_UNDERLINED :"\033[4;37m";
    public static final String WHITE_BOLD_BRIGHT_UNDERLINED =  colorsData != null ? colorsData.WHITE_BOLD_BRIGHT_UNDERLINED :"\033[4;97m";
    public static final String WHITE_BOLD_BRIGHT =  colorsData != null ? colorsData.WHITE_BOLD_BRIGHT :"\033[1;97m";
    // Underline
    public static final String BLACK_UNDERLINED =  colorsData != null ? colorsData.BLACK_UNDERLINED :"\033[4;30m";  // BLACK

    public static final String BLACK_BRIGHT_UNDERLINED =  colorsData != null ? colorsData.BLACK_BRIGHT_UNDERLINED :"\033[4;90m";  // BLACK
    public static final String RED_UNDERLINED =  colorsData != null ? colorsData.RED_UNDERLINED :"\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED =  colorsData != null ? colorsData.GREEN_UNDERLINED :"\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED =  colorsData != null ? colorsData.YELLOW_UNDERLINED :"\033[4;33m"; // YELLOW
    public static final String YELLOW_BRIGHT_UNDERLINE =  colorsData != null ? colorsData.YELLOW_BRIGHT_UNDERLINE :"\033[4;93m";// YELLOW
    public static final String BLUE_UNDERLINED =  colorsData != null ? colorsData.BLUE_UNDERLINED :"\033[4;34m";   // BLUE

    public static String PURPLE_UNDERLINED =  colorsData != null ? colorsData.PURPLE_UNDERLINED :"\033[4;35m";
    //public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED =  colorsData != null ? colorsData.CYAN_UNDERLINED :"\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED =  colorsData != null ? colorsData.WHITE_UNDERLINED :"\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND =  colorsData != null ? colorsData.BLACK_BACKGROUND :"\033[40m";  // BLACK
    public static final String RED_BACKGROUND =  colorsData != null ? colorsData.RED_BACKGROUND :"\033[41m";    // RED
    public static final String GREEN_BACKGROUND =  colorsData != null ? colorsData.GREEN_BACKGROUND :"\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND =  colorsData != null ? colorsData.YELLOW_BACKGROUND :"\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND =  colorsData != null ? colorsData.BLUE_BACKGROUND :"\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND =  colorsData != null ? colorsData.PURPLE_BACKGROUND :"\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND =  colorsData != null ? colorsData.CYAN_BACKGROUND :"\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND =  colorsData != null ? colorsData.WHITE_BACKGROUND :"\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT =  colorsData != null ? colorsData.BLACK_BRIGHT :"\033[0;90m";  // BLACK
    public static final String RED_BRIGHT =  colorsData != null ? colorsData.RED_BRIGHT :"\033[0;91m";    // RED
    public static final String GREEN_BRIGHT =  colorsData != null ? colorsData.GREEN_BRIGHT :"\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT =  colorsData != null ? colorsData.YELLOW_BRIGHT :"\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT =  colorsData != null ? colorsData.BLUE_BRIGHT :"\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT =  colorsData != null ? colorsData.PURPLE_BRIGHT :"\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT =  colorsData != null ? colorsData.CYAN_BRIGHT :"\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT =  colorsData != null ? colorsData.WHITE_BRIGHT :"\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT =  colorsData != null ? colorsData.BLACK_BOLD_BRIGHT :"\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT =  colorsData != null ? colorsData.RED_BOLD_BRIGHT :"\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT =  colorsData != null ? colorsData.GREEN_BOLD_BRIGHT :"\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT =  colorsData != null ? colorsData.YELLOW_BOLD_BRIGHT :"\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT =  colorsData != null ? colorsData.BLUE_BOLD_BRIGHT :"\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT =  colorsData != null ? colorsData.PURPLE_BOLD_BRIGHT :"\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT =  colorsData != null ? colorsData.CYAN_BOLD_BRIGHT :"\033[1;96m";  // CYAN
     // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.BLACK_BACKGROUND_BRIGHT :"\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.RED_BACKGROUND_BRIGHT :"\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.GREEN_BACKGROUND_BRIGHT :"\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.YELLOW_BACKGROUND_BRIGHT :"\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.BLUE_BACKGROUND_BRIGHT :"\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.PURPLE_BACKGROUND_BRIGHT :"\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.CYAN_BACKGROUND_BRIGHT :"\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT =  colorsData != null ? colorsData.WHITE_BACKGROUND_BRIGHT :"\033[0;107m";   // WHITE
}
