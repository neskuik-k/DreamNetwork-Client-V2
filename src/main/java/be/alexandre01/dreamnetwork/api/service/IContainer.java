package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;

import java.util.ArrayList;
import java.util.HashMap;

public interface IContainer {
    IJVMExecutor getJVMExecutor(String processName, BundleData bundleData);

    IJVMExecutor getJVMExecutor(String processName, String bundleName) throws NullPointerException;

    ArrayList<IJVMExecutor> getJVMExecutors();
    IJVMExecutor[] getJVMExecutorsFromName(String processName);

    public enum JVMType {
        SERVER, PROXY
    }

    public IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile,BundleData bundleData);

    public void stop(String name,String pathName);
}
