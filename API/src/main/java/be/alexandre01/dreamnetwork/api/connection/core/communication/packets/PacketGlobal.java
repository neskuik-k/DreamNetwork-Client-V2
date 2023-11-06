package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 21:42
*/
@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketGlobal {
    String header() default "";
    String suffix() default "#";
    PacketType castType() default PacketType.SMART;
    PacketCastOption castOption() default PacketCastOption.NOT_NULL;

    Class<?> requestClass() default Object.class;


    enum PacketType {
        // Field = helloWorld => key = helloWorld
        NORMAL,

        // Field = helloWorld => key = HelloWorld
        PRETTY,
        // Field = hello => key = HELLOWORLD
        CAPITALIZED,
        // Field = hello => key = helloworld
        LOWERCASE,


        // ALL FIELD is Accepted
        SMART;
    }

    enum PacketCastOption {
        NOT_NULL,
        NULLABLE,
        IGNORE_ALL;
    }
}
