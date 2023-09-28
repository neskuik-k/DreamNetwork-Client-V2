package be.alexandre01.dreamnetwork.api.commands.sub;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.commands.Command;

import be.alexandre01.dreamnetwork.api.console.jline.completors.CustomTreeCompleter;
import org.jline.builtins.Completers;

import java.util.ArrayList;

public class SubCommandCompletor {
    public ArrayList<Object> sub = new ArrayList<>();
    public CustomTreeCompleter.Node node;
    public Object value;

    public SubCommandCompletor(){
    }
    public SubCommandCompletor(Command command){
        this.value = command.getCompletorValue();
    }

    public void addCompletor(Object... content){
        sub.add(content);
    }

    public void update(){
        ArrayList<CustomTreeCompleter.Node> nodes = new ArrayList<>();
        sub.forEach(objects -> {
            if(objects instanceof Completers.TreeCompleter.Node){
                CustomTreeCompleter.Node node = (CustomTreeCompleter.Node) objects;
                nodes.add(node);
            }
        });
    }


    public void setCompletion(CustomTreeCompleter.Node node){

        DNUtils.get().getConsoleManager().getConsoleReader().getNodes().add(this.node = node);
        //Completer c = new Completers.TreeCompleter(node);

    }
    public enum Type{
        TEXT,
        NUMBER,
        SERVERS,
        NULL, CUSTOM;
    }
}
