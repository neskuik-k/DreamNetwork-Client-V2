package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 21:37
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface PacketCast {
    String key();

    PacketCastOption castOption() default PacketCastOption.NOT_SET;

    enum PacketCastOption {
        NOT_SET,
        NOT_NULL,
        NULLABLE,
        IGNORE_ALL,
    }
}
