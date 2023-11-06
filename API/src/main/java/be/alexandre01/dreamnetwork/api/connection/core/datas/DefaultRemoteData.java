package be.alexandre01.dreamnetwork.api.connection.core.datas;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;

import java.util.List;
import java.util.function.Consumer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 15:28
*/


public class DefaultRemoteData<T> extends RemoteData<T> {

    public DefaultRemoteData(String key, T data, UniversalConnection connection) {
        super(key, data, connection);
    }

    public DefaultRemoteData(String key, UniversalConnection connection) {
        super(key, connection);
    }

    public void setData(Object value){
        super.data = (T) value;
        getConsumers().forEach(consumer -> consumer.accept((T) value));
    }

    public List<Consumer<T>> getConsumers(){
        return super.consumers;
    }
}
