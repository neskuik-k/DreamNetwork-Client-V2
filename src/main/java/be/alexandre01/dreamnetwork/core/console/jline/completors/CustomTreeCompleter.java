package be.alexandre01.dreamnetwork.core.console.jline.completors;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.language.ColorsConverter;
import lombok.Getter;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTreeCompleter extends Completers.TreeCompleter {
    public static HashMap<String, String> colors = new HashMap<>();


    public CustomTreeCompleter(Node... objs) {
        super(objs);
    }
    public static CustomTreeCompleter.Node node(Object... objs) {
        org.jline.reader.Completer comp = null;
        List<Candidate> cands = new ArrayList();
        List<Completers.TreeCompleter.Node> nodes = new ArrayList();
        Object[] var4 = objs;
        int var5 = objs.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Object obj = var4[var6];
            if (obj instanceof String) {
                final String msgWithoutColorCodes = (String) obj.toString().replaceAll("\u001B\\[[;\\d]*m", "");


                cands.add(new Candidate(msgWithoutColorCodes, obj.toString(), (String)null, (String)null, (String)null, (String)null,true));
            } else if (obj instanceof Candidate) {
                cands.add((Candidate)obj);
            } else if (obj instanceof Completers.TreeCompleter.Node) {
                nodes.add((Completers.TreeCompleter.Node)obj);
            } else {
                if (!(obj instanceof org.jline.reader.Completer)) {
                    throw new IllegalArgumentException();
                }

                comp = (org.jline.reader.Completer)obj;
            }
        }

        if (comp != null) {
            if (!cands.isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                return new CustomTreeCompleter.Node(cands,comp, nodes);
            }
        } else if (!cands.isEmpty()) {
            return new CustomTreeCompleter.Node(cands,(r, l, c) -> {
                c.addAll(cands);
            }, nodes);
        } else {
            throw new IllegalArgumentException();
        }
    }
    @Getter
    public static class Node extends Completers.TreeCompleter.Node{
        private final org.jline.reader.Completer completer;
        private final List<Completers.TreeCompleter.Node> nodes;
        private final List<Candidate> candidates = new ArrayList<>();
        public Node(List<Candidate> candidates,org.jline.reader.Completer completer, List<Completers.TreeCompleter.Node> nodes) {
            super(completer, nodes);
            this.completer = completer;
            this.nodes = nodes;
            this.candidates.addAll(candidates);
        }
    }

    public static String convert(String coloredText,String noColorText){
        // compare two text and extract the differences
        if(coloredText.equals(noColorText)){
            return coloredText;
        }

        String[] coloredTextArray = coloredText.split(" ");
        String[] noColorTextArray = noColorText.split(" ");
        List<String> coloredTextList = Arrays.asList(coloredTextArray);
        List<String> noColorTextList = Arrays.asList(noColorTextArray);
        List<String> differences = new ArrayList<>();
        final Pattern p = Pattern.compile("\u001B\\[[;\\d]*m");
        Matcher m = p.matcher(coloredText);

        StringBuilder sb = new StringBuilder();
        String[] split = coloredText.split("\u001B\\[[;\\d]*m");
        int i = 0;
        while (m.find()) {
            String color = m.group();
            ColorsConverter c = ColorsConverter.getFromColor(color.replace(" ",""));
            m.appendReplacement(new StringBuffer(), c.toString());
            differences.add(color);
            sb.append(split[i] + "$"+c.name().toLowerCase()+ "$");
            i++;
        }

        System.out.println("COLORED !" + coloredText);
        return sb.toString();
    }
}
