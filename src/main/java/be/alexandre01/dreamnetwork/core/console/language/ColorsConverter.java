package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import lombok.Getter;

import java.util.HashMap;

public enum ColorsConverter {
    // ANSI
    ARESET(Colors.ANSI_RESET()),
    ABLACK(Colors.ANSI_BLACK()),
    ARED(Colors.ANSI_RED()),
    AGREEN(Colors.ANSI_GREEN()),
    AYELLOW(Colors.ANSI_YELLOW),
    ABLUE(Colors.ANSI_BLUE),
    APURPLE(Colors.ANSI_PURPLE),
    ACYAN(Colors.ANSI_CYAN),
    AWHITE(Colors.ANSI_WHITE),
    APWHITE(Colors.ANSI_PWHITE),


    RESET(Colors.RESET),

    // BLACK
    BLACK(Colors.BLACK),
    BLACKBO(Colors.BLACK_BOLD),
    BLACKBRUL(Colors.BLACK_BRIGHT_UNDERLINED),
    BLACKUL(Colors.BLACK_UNDERLINED),
    BLACKBG(Colors.BLACK_BACKGROUND),
    BLACKBR(Colors.BLACK_BRIGHT),
    BLACKBOBR(Colors.BLACK_BOLD_BRIGHT),
    BLACKBGBR(Colors.BLACK_BACKGROUND_BRIGHT),

    // RED
    RED(Colors.RED),
    REDBO(Colors.RED_BOLD),
    REDUL(Colors.RED_UNDERLINED),
    REDBG(Colors.RED_BACKGROUND),
    REDBR(Colors.RED_BRIGHT),
    REDBOBR(Colors.RED_BOLD_BRIGHT),
    REDBGBR(Colors.RED_BACKGROUND_BRIGHT),

    // GREEN
    GREEN(Colors.GREEN),
    GREENBO(Colors.GREEN_BOLD),
    GREENUL(Colors.GREEN_UNDERLINED),
    GREENBG(Colors.GREEN_BACKGROUND),
    GREENBR(Colors.GREEN_BRIGHT),
    GREENBOBR(Colors.GREEN_BOLD_BRIGHT),
    GREENBGBR(Colors.GREEN_BACKGROUND_BRIGHT),

    // YELLOW
    YELLOW(Colors.YELLOW),
    YELLOWBO(Colors.YELLOW_BOLD),
    YELLOWUL(Colors.YELLOW_UNDERLINED),
    YELLOWBRUL(Colors.YELLOW_BRIGHT_UNDERLINE),
    YELLOWBG(Colors.YELLOW_BACKGROUND),
    YELLOWBR(Colors.YELLOW_BRIGHT),
    YELLOWBOBR(Colors.YELLOW_BOLD_BRIGHT),
    YELLOWBGBR(Colors.YELLOW_BACKGROUND_BRIGHT),

    // BLUE
    BLUE(Colors.BLUE),
    BLUEUL(Colors.BLUE_UNDERLINED),
    BLUEBO(Colors.BLUE_BOLD),
    BLUEBG(Colors.BLUE_BACKGROUND),
    BLUEBR(Colors.BLUE_BRIGHT),
    BLUEBOBR(Colors.BLUE_BOLD_BRIGHT),
    BLUEBGBR(Colors.BLUE_BACKGROUND_BRIGHT),

    // PURPLE
    PURPLE(Colors.PURPLE),
    PURPLEBO(Colors.PURPLE_BOLD),
    PURPLEUL(Colors.PURPLE_UNDERLINED()),
    PURPLEBG(Colors.PURPLE_BACKGROUND),
    PURPLEBR(Colors.PURPLE_BRIGHT),
    PURPLEBOBR(Colors.PURPLE_BOLD_BRIGHT),
    PURPLEBGBR(Colors.PURPLE_BACKGROUND_BRIGHT),

    // CYAN
    CYAN(Colors.CYAN),
    CYANBO(Colors.CYAN_BOLD),
    CYANUL(Colors.CYAN_UNDERLINED),
    CYANBG(Colors.CYAN_BACKGROUND),
    CYANBR(Colors.CYAN_BRIGHT),
    CYANBOBR(Colors.CYAN_BOLD_BRIGHT),
    CYANBGBR(Colors.CYAN_BACKGROUND_BRIGHT),

    // WHITE
    WHITE(Colors.WHITE),
    WHITEBO(Colors.WHITE_BOLD),
    WHITEBOUL(Colors.WHITE_BOLD_UNDERLINED),
    WHITEUL(Colors.WHITE_UNDERLINED),
    WHITEBG(Colors.WHITE_BACKGROUND),
    WHITEBR(Colors.WHITE_BRIGHT),
    WHITEBOBR(Colors.WHITE_BOLD_BRIGHT),
    WHITEBGBR(Colors.WHITE_BACKGROUND_BRIGHT);

    @Getter
    static final HashMap<String,String> colors = new HashMap<>();
    @Getter private final String color;

    static {
        for(ColorsConverter color : ColorsConverter.values()){
            colors.put(color.getColor(),color.toString().toUpperCase());
        }
    }

    public static ColorsConverter getFromColor(String color){
        if(colors.containsKey(color)){
            System.out.println(colors.get(color));
            return ColorsConverter.valueOf(colors.get(color));
        }
        return null;
    }
    ColorsConverter(String color){
        this.color = color;
    }
}
