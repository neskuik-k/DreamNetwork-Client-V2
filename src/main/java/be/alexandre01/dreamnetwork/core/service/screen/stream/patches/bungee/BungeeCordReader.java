package be.alexandre01.dreamnetwork.core.service.screen.stream.patches.bungee;

import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;

public class BungeeCordReader implements IScreenInReader.ReaderLine {
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
