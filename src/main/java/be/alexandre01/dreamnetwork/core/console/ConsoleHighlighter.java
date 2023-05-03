package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.api.commands.ICommand;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.ColorsConverter;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleHighlighter implements Highlighter {
    String[] rainbow = new String[]{Colors.CYAN_BOLD, Colors.GREEN_BOLD, Colors.YELLOW_BOLD, Colors.RED_BOLD, Colors.PURPLE_BOLD, Colors.BLUE_BOLD};
    List<String> colorUsed = new ArrayList<>();

    @Override
    public AttributedString highlight(LineReader lineReader, String s) {

        AttributedStringBuilder asb = new AttributedStringBuilder();


        String[] args = s.split(" ");

        if(args.length == 0){
            lineReader.getBuffer().clear();
            return asb.toAttributedString();
        }

        if(!Console.actualConsole.startsWith( "s:")){
            lineReader.getBuffer().clear();
            lineReader.getBuffer().write(s = s.replaceAll("( )+", " "));
        }


        if (Main.getCommandReader().getCommands().getCommandsManager().executorList.containsKey(args[0].toLowerCase())) {
            ICommand command = Main.getCommandReader().getCommands().getCommandsManager().executorList.get(args[0].toLowerCase());
            asb.ansiAppend(command.getBaseColor());

            asb.append(args[0]);
            //  colorUsed.add(command.getBaseColor());
            asb.ansiAppend(Colors.RESET);
            if (Main.getGlobalSettings().isRainbowText()) {
                List<String> used = new ArrayList<>(colorUsed);
                colorUsed.clear();
                ArrayList<String> colors = new ArrayList<>(Arrays.asList(rainbow));

                //Remove the base color from the rainbow
                colors.remove(command.getBaseColor());
                if (args.length > 1) {
                    for (int i = 1; i < args.length; i++) {
                        String c;
                        if (used.isEmpty()) {
                            if (colors.isEmpty()) {
                                colors = new ArrayList<>(Arrays.asList(rainbow));
                                Collections.shuffle(colors);
                            }
                            c = colors.get(0);
                            colors.remove(0);
                        } else {
                            c = used.get(0);
                            used.remove(0);
                            colors.remove(c);
                        }


                        asb.appendAnsi(c);
                        colorUsed.add(c);
                        asb.append(" ");
                        asb.append(args[i]);
                    }

                }
                Collections.shuffle(colors);
            } else {
                for (int i = 1; i < args.length; i++) {
                    asb.append(" ");
                    asb.append(args[i]);
                }
            }
            return asb.toAttributedString();
        }

        asb.append(s);


        //args[0] = convert(args[0]);


        //asb.append(convert(s));*/
        return asb.toAttributedString();
    }


    @Override
    public void setErrorPattern(Pattern pattern) {

    }

    @Override
    public void setErrorIndex(int i) {

    }

    public static String convert(String line) {
        //for(ColorsConverter color : ColorsConverter.values()){line = line.replace("$" + color.toString().toLowerCase() + "$", color.getColor());}
        final Pattern p = Pattern.compile("\u001B\\[[;\\d]*m");
        final Matcher m = p.matcher(line);
        StringBuilder sb = new StringBuilder();
        String[] split = line.split("\u001B\\[[;\\d]*m");
        int i = 0;
        while (m.find()) {
            String color = m.group();
            ColorsConverter c = ColorsConverter.getFromColor(color.replace(" ", ""));
            m.appendReplacement(new StringBuffer(), c.toString());
            sb.append(split[i]);
            i++;
        }
        return sb.toString();
    }
}
