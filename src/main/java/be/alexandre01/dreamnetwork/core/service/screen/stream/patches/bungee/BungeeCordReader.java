package be.alexandre01.dreamnetwork.core.service.screen.stream.patches.bungee;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.screen.stream.ScreenInReader;

public class BungeeCordReader implements ScreenInReader.ReaderLine {
    @Override
    public String readLine(String line) {
        String convert = line.replaceAll("( )","").replaceAll("\\p{C}", "").replace("[m","").trim();
            if(convert.equals(">")){
                return null;
            }
            if(convert.isEmpty()){
                return null;
            }

        return line;
    }
}
