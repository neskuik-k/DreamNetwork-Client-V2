package be.alexandre01.dreamnetwork.api.utils.optional;

import be.alexandre01.dreamnetwork.api.console.Console;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 22/11/2023 at 23:20
*/
public class Facultative {

    /**
     * @param optional
     * @param action
     * @param <T>
     */
    // This method is used to check if the optional is present or not
    // This replaces the java 8 Optional class because it doesn't have a ifPresentOrElse method like the java 9 Optional class
    public static <T> void ifPresentOrElse(Optional<T> optional, Consumer<? super T> action, Runnable emptyAction){
        Object o = optional.orElse(null);
        if(o != null){
            action.accept((T) o);
        }else{
            emptyAction.run();
        }
    }
    public static <T> void ifPresentOrThrow(Optional<T> optional, Consumer<? super T> action, Supplier<? extends Throwable> emptyAction)  {
        Object o = optional.orElse(null);
        if(o != null){
            action.accept((T) o);
        }else{
            try {
                throw emptyAction.get();
            } catch (Throwable e) {
                Console.bug(e);
            }
        }
    }

    /**
     * @param optional
     * @param action
     */
    // This method is used to check if the optional is not present
    public static void ifNotPresent(Optional optional, Runnable action){
        if(!optional.isPresent()){
            action.run();
        }
    }

    public static <T> Optional<T> map(Class<T> clazz, Supplier<T> supplier){

        return  Optional.ofNullable(supplier.get());
    }



}
