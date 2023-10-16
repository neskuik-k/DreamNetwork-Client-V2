package be.alexandre01.dreamnetwork.api.service.bundle;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.external.CoreNetServer;
import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;

import com.google.common.collect.Table;
import org.jvnet.hk2.component.MultiMap;

import java.util.HashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 18:27
*/
public interface IBundleManager {
    BundleData getBundleData(String name);

    void addBundleData(BundleData bundleData);


    void addVirtualBundleData(BundleData bundleData, ExternalClient externalClient);

    void addPath(String path);

    java.util.HashMap<String, BundleData> getBundleDatas();
    Table<ExternalClient,String,BundleData> getVirtualBundles();

    Table<ExternalClient,String,String> getBundlesNamesByTool();

    java.util.ArrayList<String> getPaths();

    java.io.File getBundleIndexFile();
}
