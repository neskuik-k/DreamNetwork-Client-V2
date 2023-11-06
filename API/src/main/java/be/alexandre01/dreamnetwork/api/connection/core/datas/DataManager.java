package be.alexandre01.dreamnetwork.api.connection.core.datas;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import lombok.Getter;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 15:18
*/
public class DataManager {
    private final UniversalConnection connection;

    @Getter private final HashMap<String, RemoteData<?>> datas = new HashMap<>();

    public DataManager(UniversalConnection connection){
        this.connection = connection;
    }



    public <T> RemoteData<T> find(String key, Class<T> tClass){
        if(datas.containsKey(key)){
            return (RemoteData<T>) datas.get(key);
        }
        RemoteData<T> remoteData = new RemoteData<>(key,connection);
        datas.put(key,remoteData);
        return remoteData;
    }
}
