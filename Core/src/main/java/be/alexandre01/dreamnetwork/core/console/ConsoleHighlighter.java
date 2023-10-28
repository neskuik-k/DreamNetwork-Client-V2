package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.commands.ICommand;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleHighlighter;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.console.language.ColorsConverter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleHighlighter implements IConsoleHighlighter {
    String[] rainbow = new String[]{Colors.CYAN_BOLD, Colors.GREEN_BOLD, Colors.YELLOW_BOLD, Colors.RED_BOLD, Colors.PURPLE_BOLD, Colors.BLUE_BOLD};
    List<String> colorUsed = new ArrayList<>();

    boolean emojiOnCommand = DNUtils.get().getConfigManager().getGlobalSettings().isEmojiOnCommand() && DNCoreAPI.getInstance().getConfigManager().getGlobalSettings().isUseEmoji();


    String writing = Main.getLanguageManager().getActualLanguage().translateTo("console.dreamnetworkWriting", Core.getUsername());
    String last = "";

    @Setter
    boolean enabled = true;





    @Override
    public AttributedString highlight(LineReader lineReader, String s) {
        if (!Console.actualConsole.startsWith("s:")) {
            lineReader.getBuffer().clear();
            lineReader.getBuffer().write(s = s.replaceAll("( )+", " "));
        }
        if(!enabled){
            return new AttributedString(s);
        }
        AttributedStringBuilder asb = new AttributedStringBuilder();


        String[] args = s.split(" ");


        if (args.length == 0) {
            lineReader.getBuffer().clear();
            print(asb, s);

            IConsoleReader.getReader().setPrompt(Console.getCurrent().writing);
            return asb.toAttributedString();
        }



            if (Main.getCommandReader().getCommands().getExecutorsList().containsKey(args[0].toLowerCase())) {
                ICommand command = Main.getCommandReader().getCommands().getExecutorsList().get(args[0].toLowerCase());
                // asb.append(command.getEmoji()+" ");

                if(emojiOnCommand){
                String emoji = command.getEmoji();
                if(emoji != null){
                    if (!emoji.equalsIgnoreCase("")) {
                        emoji = emoji + " ";
                    }

                    IConsoleReader.getReader().setPrompt(writing+emoji);
                }

                //Console.MAIN.setWriting(Console.getFromLang("console.dreamnetworkWriting", Core.getUsername() + emoji));

                }
                // System.out.println(Console.MAIN.writing);
                asb.ansiAppend(command.getBaseColor());


                asb.append(args[0]);
                //  colorUsed.add(command.getBaseColor());
                asb.ansiAppend(Colors.RESET);

                if(args.length > 1){
                    asb.ansiAppend(Colors.WHITE_BOLD);
                    asb.append(" ");
                    asb.append(args[1]);
                    asb.ansiAppend(Colors.RESET);
                }
                if (DNCoreAPI.getInstance().getConfigManager().getGlobalSettings().isRainbowText()) {
                    List<String> used = new ArrayList<>(colorUsed);
                    colorUsed.clear();
                    ArrayList<String> colors = new ArrayList<>(Arrays.asList(rainbow));

                    //Remove the base color from the rainbow
                    colors.remove(command.getBaseColor());
                    if (args.length > 3) {
                        for (int i = 2; i < args.length; i++) {
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
                    for (int i = 2; i < args.length; i++) {
                        asb.append(" ");
                        asb.append(args[i]);
                    }
                }
                //ConsoleReader.sReader.getBuffer().clear();
                // ConsoleReader.sReader.getBuffer().write(s = s.replaceAll("( )+", " "));

                print(asb, s);

                return asb.toAttributedString();
            }

        asb.append(s);


        //args[0] = convert(args[0]);


        print(asb, s);
        //asb.append(convert(s));*/
        IConsoleReader.getReader().setPrompt(Console.getCurrent().writing);
        return asb.toAttributedString();
    }

    @Override
    public void print(AttributedStringBuilder asb, String s) {
        if (last != null) {
            if (last.equalsIgnoreCase(s)) {
                return;
            } else {
                last = s;
                //System.out.println("last is " + asb.toAttributedString());
            }
        } else {
            last = s;
            //  System.out.println("last is null " + asb.toAttributedString());
        }
    }

    @Override
    public void setErrorPattern(Pattern pattern) {

    }

    @Override
    public void setErrorIndex(int i) {

    }
    static String convert(String line) {
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
