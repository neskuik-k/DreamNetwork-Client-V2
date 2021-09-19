package be.alexandre01.dreamnetwork.client.commands.sub;

import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import org.jline.builtins.Completers;

import java.util.ArrayList;

public class SubCommandCompletor {
    public ArrayList<String[]> sub = new ArrayList<>();
    public void addCompletor(String... content){
        sub.add(content);
    }

    public void setCompletion(Completers.TreeCompleter.Node node){
        ConsoleReader.nodes.add(node);
    }
}
