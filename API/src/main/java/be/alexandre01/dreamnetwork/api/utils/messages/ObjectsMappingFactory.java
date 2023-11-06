package be.alexandre01.dreamnetwork.api.utils.messages;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:38
*/
public class ObjectsMappingFactory {
    private final HashMap<Class<?>, ObjectConverterMapper<?,?>> mapperHashMap = new HashMap<>();
    public void addMapper(ObjectConverterMapper<?,?>... mapper){
        for(ObjectConverterMapper<?,?> objectConverterMapper : mapper){
            mapperHashMap.put(objectConverterMapper.getClass(), objectConverterMapper);
        }
    }

    public void removeMapper(ObjectConverterMapper<?,?> mapper){
        mapperHashMap.remove(mapper.getClass());
    }


    @SuppressWarnings("unchecked")
    public ObjectConverterMapper<?,?> getMapper(Class<?> clazz){
        ObjectConverterMapper<?,?> mapper = mapperHashMap.get(clazz);
        if(mapper != null){
            if(mapper.getClass().getTypeParameters()[0].getClass() == clazz){
                return (ObjectConverterMapper<?, ?>) mapper;
            }
        }
        return null;
    }
}
