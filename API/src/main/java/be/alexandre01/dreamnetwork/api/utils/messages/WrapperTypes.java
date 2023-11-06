package be.alexandre01.dreamnetwork.api.utils.messages;

import java.util.HashSet;
import java.util.Set;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 13:37
*/
public class WrapperTypes {
    public static Set<Class<?>> get()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
}
