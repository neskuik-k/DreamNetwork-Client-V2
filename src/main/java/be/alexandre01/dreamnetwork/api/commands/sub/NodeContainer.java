package be.alexandre01.dreamnetwork.api.commands.sub;


import be.alexandre01.dreamnetwork.utils.Tuple;

import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;

import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class NodeContainer {

    private NodeBuilder linkNodeBuilder;
    private Object[] objects;
    @Getter private final HashMap<Object, Tuple<Integer,Integer>> index = new HashMap<>();
    @Getter private List<Object> list;
    private List<Candidate> candidates;

    public NodeContainer(Object... objects){
        this.objects = objects;
    }

    public static NodeContainer of(Object... objects){
        return new NodeContainer(objects);
    }
}
