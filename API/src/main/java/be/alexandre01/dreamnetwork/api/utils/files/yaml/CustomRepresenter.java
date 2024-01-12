package be.alexandre01.dreamnetwork.api.utils.files.yaml;

import be.alexandre01.dreamnetwork.api.console.Console;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.FieldProperty;
import org.yaml.snakeyaml.introspector.GenericProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Field;
import java.util.*;

public class CustomRepresenter extends Representer {

    private final boolean skipNull;
    private final Class[] clazz;

    @Setter private Object obj;
    @Setter private boolean thisClassOnly = false;


    public CustomRepresenter(boolean skipNull,Object obj,Class<?>... clazz) {
        super(new DumperOptions());
        this.skipNull = skipNull;
        this.clazz = clazz;
        this.obj = obj;
        /*PropertyUtils propUtil = new PropertyUtils() {
            @Override
            protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
                return getPropertiesMap(type, bAccess).values().stream().sequential()
                        .filter(prop -> prop.isReadable() && (isAllowReadOnlyProperties() || prop.isWritable()))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
        };
        setPropertyUtils(propUtil);*/
    }

    public CustomRepresenter(boolean skipNull,Class<?>... clazz){
        this(skipNull,null,clazz);
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {

                 /*  System.out.println(property.getType());
                    System.out.println(propertyValue);

*/

        //check if field has annotation @Ignore



        if (propertyValue == null && skipNull) {
            return null;
        }
        ArrayList<Field> fields = new ArrayList<>();
        for (Class c : clazz){
            //System.out.println(c.getDeclaredFields());
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        boolean isFinded = false;
        for (Field field : fields) {
            //System.out.println(field.getName());
            field.setAccessible(true);

            //    System.out.println("Annotation => "+field.getAnnotation(Ignore.class));
            if (field.getAnnotation(Ignore.class) != null) {
                // Console.printLang("warning");
                                    /*System.out.println("WARNING");
                                    System.out.println(field.getName());
                                    System.out.println(property.getName());
                                    System.out.println(field.get(obj));*/
                if (field.getName().equals(property.getName())) {
                    //System.out.println("IGNORED field "+field.getName()+" because it's equals to "+property.getName());
                    // Console.printLang("core.utils.yaml.ignoreFieldEquals", field.getName(), propertyValue);
                    return null;
                }
            }

           // System.out.println(field.getName()+propertyValue);
            if (field.getName().equals(property.getName())) {
               // System.out.println("Finded field "+field.getName()+" because it's equals to "+property.getName());
                isFinded = true;
                break;
            }
            // check if boolean
            System.out.println(property.getType() + " " + field.getType() + "> " + property.getName() + " " + field.getName());
            if(property.getType().equals(boolean.class) || property.getType().equals(Boolean.class)){
                //System.out.println("Finded field "+field.getName()+" because it's equals to "+property.getName());
                String name = property.getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                name = "is"+name;
                System.out.println("Finded field "+field.getName()+" because it's equals to "+name +" ?");
                System.out.println(property.getClass());

                if (field.getName().equals(name)) {
                    //System.out.println("Finded field "+field.getName()+" because it's equals to "+property.getName());
                    isFinded = true;
                    property = new CustomFieldProperty(field,name);
                    break;
                }
            }
            //break;
        }

        if (!isFinded) {
            Console.fine(Console.getFromLang("core.utils.yaml.ignoreFieldNotFound", property.getName(), CustomRepresenter.class.getName()));
            return null;
        }
        if (obj.getClass().equals(property.getType())) {
            return null;
        } else {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }


}