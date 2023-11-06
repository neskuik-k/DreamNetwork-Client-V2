package be.alexandre01.dreamnetwork.api.utils.messages;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:33
*/
public abstract class ObjectConverterMapper<T,S> {
    public abstract S convert(T object);

    public abstract T read(S object);

    public Class<?> findConvertableTypeOf(Class<?> clazz){
        return this.getClass().getTypeParameters()[1].getClass();
    }

    public Class<?> findReadableTypeOf(Class<?> clazz){
        return this.getClass().getTypeParameters()[0].getClass();
    }
}
