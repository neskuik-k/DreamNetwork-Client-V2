package be.alexandre01.dreamnetwork.api.service.bundle;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 18:27
*/
public interface IBundleManager {
    BundleData getBundleData(String name);

    void addBundleData(BundleData bundleData);
    void addVirtualBundleData(BundleData bundleData);



    void addPath(String path);

    java.util.HashMap<String, BundleData> getBundleDatas();
    java.util.HashMap<String, BundleData> getVirtualBundles();

    Table<IClient,String,String> getBundlesNamesByTool();

    java.util.ArrayList<String> getPaths();

    java.io.File getBundleIndexFile();
}
