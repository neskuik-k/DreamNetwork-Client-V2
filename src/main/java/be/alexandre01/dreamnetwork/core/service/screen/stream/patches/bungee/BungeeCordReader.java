package be.alexandre01.dreamnetwork.core.service.screen.stream.patches.bungee;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.screen.stream.ScreenInReader;

public class BungeeCordReader implements ScreenInReader.ReaderLine {
    @Override
    public String readLine(String line) {
            if(line.contains("\n")){
                String[] args = line.split("\n");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    if(arg!= null){
                        if(arg.length() != 0){
                            if(arg.replaceAll("( )","").replaceAll("\\p{C}", "").replace("[m","").trim().equals(">")){
                                //remove this arg from string
                                continue;
                            }

                            if(i != 0)
                                sb.append(" ");
                            sb.append(arg);
                        }
                    }
                }
                line = sb.toString();
            }


        return line;
    }
}
