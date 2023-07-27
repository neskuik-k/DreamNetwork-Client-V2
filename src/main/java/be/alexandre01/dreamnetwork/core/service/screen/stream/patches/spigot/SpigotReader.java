package be.alexandre01.dreamnetwork.core.service.screen.stream.patches.spigot;

import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;

public class SpigotReader implements IScreenInReader.ReaderLine {
    @Override
    public String readLine(String line) {

//BETTER COLORS
        if(line.contains("[STDERR]") || line.contains("ERROR]:")){
            return Colors.RED+line+Colors.RESET;
        }
        if(line.contains("WARN]:")){
            return Colors.YELLOW+line+Colors.RESET;
        }
        return line;
    }
}
