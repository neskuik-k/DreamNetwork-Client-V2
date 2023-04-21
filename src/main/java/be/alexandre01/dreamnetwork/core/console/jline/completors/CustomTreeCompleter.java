package be.alexandre01.dreamnetwork.core.console.jline.completors;

import be.alexandre01.dreamnetwork.core.Core;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CustomTreeCompleter extends Completers.TreeCompleter {
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
                return new CustomTreeCompleter.Node(comp, nodes);
            }
        } else if (!cands.isEmpty()) {
            return new CustomTreeCompleter.Node((r, l, c) -> {
                c.addAll(cands);
            }, nodes);
        } else {
            throw new IllegalArgumentException();
        }
    }
    public static class Node extends Completers.TreeCompleter.Node{
        public Node(org.jline.reader.Completer completer, List<Completers.TreeCompleter.Node> nodes) {
            super(completer, nodes);
        }
    }
}
