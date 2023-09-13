package be.alexandre01.dreamnetwork.api.service.bundle;

import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;

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

    java.util.ArrayList<String> getPaths();

    java.io.File getBundleIndexFile();
}
