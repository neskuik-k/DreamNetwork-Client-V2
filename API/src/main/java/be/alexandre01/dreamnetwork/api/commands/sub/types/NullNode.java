package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeType;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import org.jline.builtins.Completers;

public class NullNode extends NodeType {
    public NullNode(){
        type = SubCommandCompletor.Type.NULL;
        objects = new Object[]{Completers.AnyCompleter.INSTANCE};
    }
}
