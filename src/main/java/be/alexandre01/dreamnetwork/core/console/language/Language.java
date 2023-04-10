package be.alexandre01.dreamnetwork.core.console.language;

import lombok.Getter;

import java.util.HashMap;

public class Language {
    private final String localizedName;
    @Getter private final HashMap<String,String> messages = new HashMap<>();

    public Language(String localizedName) {
        this.localizedName = localizedName;
    }

    public static void main(String[] args) {
        String s = "test du serveur %s sur le port %s";
        int i = 50;
        String serv = "TropBien";
        System.out.println(String.format(s,serv,i));
    }
    public String translateTo(String map,Object... variables){
        if(variables.length == 0){
            return messages.get(map);
        }
        return String.format(messages.get(map),variables);
    }
}
