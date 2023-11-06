package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import be.alexandre01.dreamnetwork.api.utils.messages.Message;

import java.lang.annotation.Annotation;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 11:56
*/
@SuppressWarnings("all")
public interface RequestExample<T> extends RequestAnnotation{
    default String getProjectName(){
        return "null";
    }

    default String getSuffix(){
        return "#";
    }
    default String value(){
        if(getOrdinal() == -1){
            return getProjectName()+getSuffix()+ getProjectName().hashCode();
        }
        return getProjectName()+getSuffix()+ getOrdinal();
    }

    default int getOrdinal(){
        return -1;
    }


    @Override
    public default Class<? extends Annotation> annotationType() {
        return (Class<? extends Annotation>) RequestExample.class.getTypeParameters()[0].getClass();
    }

    public default Message getMessage(){
        return new Message(value());
    }
}
