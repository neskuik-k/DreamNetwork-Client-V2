package be.alexandre01.dreamnetwork.core.console.language;

import lombok.Getter;

import java.util.HashMap;

public class Language {
    @Getter private final String localizedName;
    @Getter private final HashMap<String,String> messages = new HashMap<>();

    public Language(String localizedName) {
        this.localizedName = localizedName;
    }

    public static void main(String[] args) {
        String msg = "Installation of %cyan% %s %areset%  %s [%s mb]";
        String bar = "[<->                              ]";
        String space = "e";
        String kb = "500";
        int i = 50;
        String serv = "TropBien";
        System.out.println(String.format(msg,bar,kb,space));
    }
    public String translateTo(String map,Object... variables){
        if(variables.length == 0){
            return messages.get(map);
        }

        String msg = messages.get(map);
       // System.out.println("Message to encode => "+msg);
        for (int i = 0; i < variables.length; i++) {
            String var;
            if(variables[i] == null){
                var = "null";
            }else{
                var = variables[i].toString();
            }
            msg = msg.replace("%var"+i+"%",var);
        }
        return msg;
    }
}
