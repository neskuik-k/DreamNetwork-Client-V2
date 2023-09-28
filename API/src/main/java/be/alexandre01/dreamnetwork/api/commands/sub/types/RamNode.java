package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;

import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import org.jline.reader.Candidate;

import java.util.ArrayList;
import java.util.List;

public class RamNode extends CustomType {

    List<Candidate> list = new ArrayList<>();

    public RamNode(int minValue,boolean isInGB) {
        String unit = isInGB ? "G" : "M";
        int value = isInGB ? 1 : 256;
        int j = 1;
        for (int i = 1; i < (isInGB ? 5 : 8); i++) {
            if(value >= minValue){
                Candidate c = new Candidate(    value+unit, Colors.CYAN_BOLD_BRIGHT+value+Colors.YELLOW_UNDERLINED+unit,null,null,null,null,true ,j);
                list.add(c);
                j++;
            }
            value*=2;
        }
        type = SubCommandCompletor.Type.CUSTOM;

        setCustomType(() -> {
            return list.toArray(new Candidate[0]);
        });
    }

    public RamNode(int minValue) {
        this(minValue,false);
    }
}
