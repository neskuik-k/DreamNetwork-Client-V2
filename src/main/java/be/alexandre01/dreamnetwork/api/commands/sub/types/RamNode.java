package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;

import org.apache.commons.lang.ArrayUtils;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;

import java.util.ArrayList;
import java.util.List;

public class RamNode extends CustomType {

    List<Candidate> list = new ArrayList<>();

    public RamNode(int minValue) {
        int value = 256;
        int j = 1;
        for (int i = 1; i < 8; i++) {
            if(value >= minValue){
                Candidate c = new Candidate(    value+"M",Colors.CYAN_BOLD_BRIGHT+value+Colors.YELLOW_UNDERLINED+"M",null,null,null,null,true ,j);
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
}
