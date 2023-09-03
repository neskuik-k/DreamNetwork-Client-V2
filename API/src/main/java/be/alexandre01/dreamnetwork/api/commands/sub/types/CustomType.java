package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeType;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.utils.Tuple;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CustomType extends NodeType {

    @Getter private static final Multimap<Class<? extends CustomType>,Object> customTypes = ArrayListMultimap.create();

    private int[] index;

    @Getter @Setter
    CustomTypeInterface customType;

    @Getter @Setter
    private NodeContainer linkNodeContainer;

    @Getter @Setter
    private Completers.TreeCompleter.Node nodeAbove;

    @Getter @Setter
    private ArrayList<Object> globalObjects = new ArrayList<>();


    public CustomType(Object... objects){
        super.objects = objects;
        super.type = SubCommandCompletor.Type.CUSTOM;
        customTypes.put(getClass(),this);
    }

    public static void reloadAll(Class<? extends CustomType>... customTypeClass){
        ArrayList<NodeBuilder> nodeBuilders = new ArrayList<>();
        for(Class<? extends CustomType> customType : customTypeClass){
            for (Object o : customTypes.get(customType)) {
                CustomType c = (CustomType) o;
                if(c.getLinkNodeContainer() != null){
                    nodeBuilders.add(c.getLinkNodeContainer().getLinkNodeBuilder());
                }

                //  customType.reload();
            }
            Console.fine("Reload " + customType.getSimpleName());
            final int[] i = {0};
            try {
                nodeBuilders.forEach(nodeBuilder -> {
                    if(nodeBuilder == null){
                        Console.fine("NodeBuilder is null");
                        return;
                    }

                    //Find NodeBuilders
                   // Console.fine("Find " + nodeBuilder.getClass().getSimpleName()+" "+ i[0]);
                    nodeBuilder.rebuild();
                    i[0]++;
                });
            }catch (Exception e){
               Console.bug(e);
            }

           // nodeBuilders.forEach(NodeBuilder::rebuild);



        }

    }

    public Object[] reload(){

        Object[] objects = customType.find();
       // System.out.println(linkNodeContainer + "");
       // System.out.println(linkNodeContainer.getIndex());
        //System.out.println(linkNodeContainer.getList());
        Tuple<Integer,Integer> i = linkNodeContainer.getIndex().get(this);

        if(i != null){
           /* System.out.println(i.a() + " " + i.b());
            for (int j = i.b()-1; j > i.a()-1; j--) {
                Console.debugPrint("Remove " + j);
                linkNodeContainer.getList().remove(j);
            }
            System.out.println("La nouvelle liste "+ Arrays.asList(objects));
            for (int j = 0; j < objects.length; j++) {
                System.out.println("Add " + (i.a()+j) + " " + objects[j]);
                linkNodeContainer.getList().add(i.a()+j,objects[j]);

            }
            //linkNodeContainer.getList().addAll(i.a(), Arrays.asList(objects));

            linkNodeContainer.getLinkNodeBuilder().rebuild();
            return objects;*/
        }

        //System.out.println(Arrays.toString(objects));
        linkNodeContainer.getList().addAll(Arrays.asList(objects));

        return objects;
    }

    public interface CustomTypeInterface{
        public Object[] find();
    }


}
