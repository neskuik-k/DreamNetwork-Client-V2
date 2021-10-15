package be.alexandre01.dreamnetwork.client.service.bundle;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.utils.json.JSONFileUtils;
import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class BundleIndex extends JSONFileUtils {
    @Override
    public Object put(String key,Object value){
        Object k = super.put(key,value);

        if(value instanceof BundleData){
            BundleData bundleData = (BundleData) value;
            BundleManager bundleManager = Client.getInstance().getBundleManager();
            bundleManager.getBundleDatas().add(bundleData);
        }
        if(value instanceof String){
            BundleData bundleData = BundleData.createFromHashMap( convertWithGuava((String) value));
            BundleManager bundleManager = Client.getInstance().getBundleManager();
            bundleManager.getBundleDatas().add(bundleData);
        }

        return k;
    }
    public Map<String, String> convertWithGuava(String mapAsString) {
        return Splitter.on(',').withKeyValueSeparator('=').split(mapAsString);
    }
}
