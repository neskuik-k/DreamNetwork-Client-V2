package be.alexandre01.dreamnetwork.api.commands.sub;


import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.utils.Tuple;

import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jvnet.hk2.component.MultiMap;

import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class NodeContainer {

    private NodeBuilder linkNodeBuilder;
    private Object[] objects;
    @Getter private final HashMap<Object, Tuple<Integer,Integer>> index = new HashMap<>();
    @Getter private List<Object> list;
    private List<Candidate> candidates;
    private NodeContainer parent;
    //private List<String> stringList = new ArrayList<>();
    //private HashMap<String,NodeContainer> nodeContainerHashMap = new HashMap<>();

    public NodeContainer(Object... objects){
        this.objects = objects;
        /*for (Object obj : objects) {
            if(obj instanceof NodeContainer){
                ((NodeContainer) obj).setParent(this);
            }
            if(obj instanceof String){
                stringList.add((String) obj);
            }
            if(obj instanceof Candidate){
                stringList.add(((Candidate) obj).value());
            }
            if(obj instanceof CustomType){

            }
        }*/
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
