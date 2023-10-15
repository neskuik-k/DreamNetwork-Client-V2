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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CustomType extends NodeType {

    @Getter private static final Multimap<Class<? extends CustomType>,Object> customTypes = ArrayListMultimap.create();

    private int[] index;

    @Getter @Setter
    CustomTypeInterface customType;

    @Getter
    private NodeContainer linkNodeContainer;

    @Getter @Setter
    private Completers.TreeCompleter.Node nodeAbove;

    @Getter @Setter
    private ArrayList<Object> globalObjects = new ArrayList<>();


    public void setLinkNodeContainers(NodeContainer linkNodeContainer){
        this.linkNodeContainer = linkNodeContainer;
    }

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
                    nodeBuilders.addAll(c.getLinkNodeContainer().getLinksNodeBuilder());
                    //c.getLinkNodeContainer().getLinksNodeBuilder().clear();
                }

                //  customType.reload();
            }
            Console.fine("Reload " + customType.getSimpleName());

            try {
                nodeBuilders.forEach(new Consumer<NodeBuilder>() {
                    int i = 0;
                    @Override
                    public void accept(NodeBuilder nodeBuilder) {
                        if(nodeBuilder == null){
                            Console.fine("NodeBuilder is null");
                            return;
                        }
                        //Console.fine("Find " + nodeBuilder.getClass().getSimpleName()+" "+ i);
                        //Find NodeBuilders
                        // Console.fine("Find " + nodeBuilder.getClass().getSimpleName()+" "+ i[0]);
                        nodeBuilder.rebuild();
                        i++;
                    }
                });


                Console.getCurrent().reloadCompletors();

            }catch (Exception e){
               Console.bug(e);
            }

           // nodeBuilders.forEach(NodeBuilder::rebuild);



        }

    }

    public Object[] reload(){
        Object[] objects = customType.find();
        linkNodeContainer.getList().addAll(Arrays.asList(objects));
        return objects;
    }

    public interface CustomTypeInterface{
        public Object[] find();
    }


}
