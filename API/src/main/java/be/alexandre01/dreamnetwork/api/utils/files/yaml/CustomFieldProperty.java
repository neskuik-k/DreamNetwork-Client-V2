//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package be.alexandre01.dreamnetwork.api.utils.files.yaml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.GenericProperty;
import org.yaml.snakeyaml.util.ArrayUtils;

public class CustomFieldProperty extends GenericProperty {
    private final Field field;

    public CustomFieldProperty(Field field,String customName) {
        super(customName, field.getType(), field.getGenericType());
        this.field = field;
        field.setAccessible(true);
    }

    public CustomFieldProperty(Field field) {
        this(field,field.getName());
    }

    public void set(Object object, Object value) throws Exception {
        this.field.set(object, value);
    }

    public Object get(Object object) {
        try {
            return this.field.get(object);
        } catch (Exception var3) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + var3);
        }
    }

    public List<Annotation> getAnnotations() {
        return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.field.getAnnotation(annotationType);
    }
}
