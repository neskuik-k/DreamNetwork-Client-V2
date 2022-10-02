package be.alexandre01.dreamnetwork.api.commands.sub;

import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import org.jline.builtins.Completers;

import java.util.ArrayList;

public class SubCommandCompletor {
    public ArrayList<Object> sub = new ArrayList<>();
    public Completers.TreeCompleter.Node node;
    public void addCompletor(Object... content){
        sub.add(content);
    }

    public void update(){
        ArrayList<Completers.TreeCompleter.Node> nodes = new ArrayList<>();
        sub.forEach(objects -> {
            if(objects instanceof Completers.TreeCompleter.Node){
                Completers.TreeCompleter.Node node = (Completers.TreeCompleter.Node) objects;
                nodes.add(node);
            }
        });
    }


    public void setCompletion(Completers.TreeCompleter.Node node){

        ConsoleReader.nodes.add(this.node = node);
        //Completer c = new Completers.TreeCompleter(node);

    }
    public enum Type{
        TEXT,
        NUMBER,
        SERVERS,
        NULL, CUSTOM;
    }
}
