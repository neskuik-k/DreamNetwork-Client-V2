package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.utils.json.JSONFileUtils;
import com.google.common.base.Splitter;

import java.util.Map;

public class BundleIndex extends JSONFileUtils {
    @Override
    public Object put(String key,Object value){
        Object k = super.put(key,value);

        if(value instanceof BundleData){
            BundleData bundleData = (BundleData) value;
            BundleManager bundleManager = Core.getInstance().getBundleManager();
            bundleManager.getBundleDatas().add(bundleData);
        }
        if(value instanceof String){
            BundleData bundleData = BundleData.createFromHashMap( convertWithGuava((String) value));
            BundleManager bundleManager = Core.getInstance().getBundleManager();
            bundleManager.getBundleDatas().add(bundleData);
        }

        return k;
    }
    public Map<String, String> convertWithGuava(String mapAsString) {
        return Splitter.on(',').withKeyValueSeparator('=').split(mapAsString);
    }
}
