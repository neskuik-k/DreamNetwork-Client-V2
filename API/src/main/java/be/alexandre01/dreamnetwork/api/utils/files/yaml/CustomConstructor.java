package be.alexandre01.dreamnetwork.api.utils.files.yaml;

import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class CustomConstructor extends Constructor {
    private final boolean ignorePatch;
    private final Class<?> clazz;

    @Getter
    private final List<Field> settedFields = new ArrayList<>();

    CustomConstructor(LoaderOptions loaderOptions, boolean ignorePatch, Class<?> clazz) {
        super(loaderOptions);
        this.ignorePatch = ignorePatch;
        this.clazz = clazz;
        yamlClassConstructors.put(NodeId.mapping, new NodesConstructor());
    }

    class NodesConstructor extends Constructor.ConstructMapping {
        @Override
        protected Object constructJavaBean2ndStep(MappingNode node, Object object) {

            if (!ignorePatch) {
                Class type = node.getType();

               // System.out.println("Construct object " + node.getNodeId().name());

                for (NodeTuple n : node.getValue()) {
                 //   System.out.println("Construct object " + n.getKeyNode());
                    if (n.getKeyNode() instanceof ScalarNode) {

                        ScalarNode scalarNode = (ScalarNode) n.getKeyNode();

                        try {
                           // System.out.println("Finded scalar node " + scalarNode.getValue());
                            settedFields.add(clazz.getDeclaredField(scalarNode.getValue()));
                        } catch (NoSuchFieldException e) {
                            try {
                                settedFields.add(clazz.getField(scalarNode.getValue()));
                            } catch (NoSuchFieldException e1) {
                                continue;
                            }
                        }
                    }
                }
            }
            return super.constructJavaBean2ndStep(node, object);
        }
    }
}