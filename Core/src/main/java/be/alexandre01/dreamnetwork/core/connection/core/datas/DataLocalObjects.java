package be.alexandre01.dreamnetwork.core.connection.core.datas;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 18:51
*/
public class DataLocalObjects {
    @Getter
    private final HashMap<String, Object> localDatas = new HashMap<>();
    @Getter private final Multimap<String, UniversalConnection> dataSubscribers = ArrayListMultimap.create();
    public void setLocalData(String key, Object data){
        if(localDatas.containsKey(key)){
            if(!localDatas.get(key).getClass().isAssignableFrom(data.getClass()))
                throw new RuntimeException("Data "+ key + "already set with a different type ("+ localDatas.get(key).getClass().getSimpleName() +")");
        }

        localDatas.put(key,data);
        for (UniversalConnection connection : dataSubscribers.get(key)) {
            connection.getRequestManager().sendRequest(RequestType.UNIVERSAL_SEND_DATA,key,data);
        }
    }

    public Object getLocalData(String key){
        return localDatas.get(key);
    }

    public <T> T getLocalData(String key, Class<T> tClass){
        return (T) localDatas.get(key);
    }
}
