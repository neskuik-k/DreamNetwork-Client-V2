package be.alexandre01.dreamnetwork.client.commands.sub;

import java.util.ArrayList;

public class SubCommandCompletor {
    public ArrayList<String[]> sub = new ArrayList<>();
    public void addCompletor(String... content){
        sub.add(content);
    }
}
