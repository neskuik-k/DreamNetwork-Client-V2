package be.alexandre01.dreamnetwork.api.commands.sub;


import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.utils.Tuple;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class NodeContainer {

    private final List<NodeBuilder> linksNodeBuilder = new ArrayList<>();
    private Object[] objects;
    @Getter private final HashMap<Object, Tuple<Integer,Integer>> index = new HashMap<>();
    @Getter private List<Object> list;
    @Getter private HashMap<String,Object> strings = new HashMap<>();
    private List<Candidate> candidates;
    //private List<String> stringList = new ArrayList<>();
    //private HashMap<String,NodeContainer> nodeContainerHashMap = new HashMap<>();

    public NodeContainer(Object... objects){
        this.objects = objects;
    }

    public NodeContainer(String... strings){
        this.objects = strings;
    }
  /*  public void setParent(NodeContainer parent){
        this.parent = parent;

        for (String s : stringList) {
            parent.nodeContainerHashMap.put(s,this);
        }

    }

    public NodeContainer getFromString(String arg){
          return nodeContainerHashMap.get(arg);
    }

    public NodeContainer getFromStrings(String... args){
        NodeContainer nodeContainer = this;
        for (String arg : args) {
            nodeContainer = nodeContainer.getFromString(arg);
            if(nodeContainer == null) return null;
        }
        return nodeContainer;
    }*/

    public static NodeContainer of(Object... objects){
        return new NodeContainer(objects);
    }
}
