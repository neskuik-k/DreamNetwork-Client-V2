package be.alexandre01.dreamnetwork.api.commands.sub;

import be.alexandre01.dreamnetwork.api.commands.CommandReader;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.commands.sub.types.NullNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.TextNode;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.utils.Tuple;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.jline.builtins.Completers;


import java.util.*;
import java.util.logging.Level;

public class NodeBuilder {
    Completers.TreeCompleter.Node node;

    Console console;

    List<Object> globalList;
    int num  =0;
    HashMap<Completers.TreeCompleter.Node,Object[]> nodes = new HashMap<>();


    public static NullNode EMPTYFIELD = new NullNode();
    NodeContainer nodeContainer;

    public static NodeContainer create(Object... o){
        return new NodeContainer(o);
    }


    public NodeBuilder(NodeContainer nodeContainer,Console console){
        this.console = console;
        this.nodeContainer = nodeContainer;
        node = genNode(nodeContainer,true);
        registerTo();
    }
    public NodeBuilder(NodeContainer nodeContainer){
        this(nodeContainer,Console.getConsole("m:default"));
    }


    private void addNode(Completers.TreeCompleter.Node node, Object[] objects){
        nodes.put(node,objects);
    }
    public Completers.TreeCompleter.Node genNode(NodeContainer nodeContainer,boolean isHead){
        final Object objects[] = nodeContainer.getObjects();
        final ArrayList<Object> list = new ArrayList<>();
        nodeContainer.setList(list);
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];

            if(o instanceof NodeContainer){
                ((NodeContainer) o).setLinkNodeBuilder(this);
                list.add(genNode((NodeContainer) o,false));
                continue;
            }

            if(o instanceof CustomType){
                CustomType customType = (CustomType) o;
                customType.setLinkNodeContainer(nodeContainer);
                customType.setGlobalObjects(list);
                int currentSize = list.size();

                list.addAll(Arrays.asList((customType.reload())));
                nodeContainer.getIndex().put(o,new Tuple<>(currentSize,list.size()));

                continue;
            }

            if(o instanceof NullNode){
                list.add(EMPTYFIELD);
                continue;
            }

            if(o instanceof TextNode){
                list.addAll(Arrays.asList(((TextNode) o).getTexts()));
                continue;
            }
            list.add(o);
        }
        //System.out.println("AFTER");
        if(isHead){
            globalList = list;
        }
       Console.fine("Build Suggestion -> " + list);
        Completers.TreeCompleter.Node n = Completers.TreeCompleter.node(list.toArray());
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            if(o instanceof CustomType){
                CustomType customType = (CustomType) o;
                customType.setNodeAbove(n);
            }
        }
        //System.out.println(list);
        nodes.put(n,list.toArray());


        return n;
    }


    public void rebuild(){
        try {
            Console.fine("rebuild suggestion");
            Console.fine(globalList);
            Console.fine("ICI hein");
            for (int i = 0; i < globalList.size(); i++) {
                Object o = globalList.get(i);
                if(o instanceof NodeContainer){
                    printContainers((NodeContainer) o);

                }
                //System.out.println(o);
            }

            System.out.println(num);
            console.completorNodes.set(num, node = genNode(nodeContainer,true));
            //ConsoleReader.nodes.set(num, node = genNode(nodeContainer,true));
            if(Console.getCurrent() == console){
                ConsoleReader.reloadCompleter();
            }
            //Console.getCurrent().reloadCompletor();
            //ConsoleReader.reloadCompleter();
        }catch (Exception e){
            Console.bug(e);
        }

    }
    private void printContainers(NodeContainer nodeContainer){
        for (int i = 0; i < nodeContainer.getIndex().size(); i++) {
            Object o = nodeContainer.getList().get(i);
            if(o instanceof NodeContainer){
                printContainers((NodeContainer) o);
                continue;
            }
           Console.fine(Colors.RED+o);
        }
    }
    public void registerTo(){
        Console.fine("Register new Suggestion");
        num = console.completorNodes.size();
       // ConsoleReader.nodes.add(node);
        console.completorNodes.add(node);
    }
}
