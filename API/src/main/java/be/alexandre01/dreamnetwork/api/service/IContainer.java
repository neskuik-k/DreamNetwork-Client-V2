package be.alexandre01.dreamnetwork.api.service;


import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface IContainer {
    IExecutor getJVMExecutor(String processName, BundleData bundleData);

    IExecutor getJVMExecutor(String processName, String bundleName) throws NullPointerException;

    ArrayList<IExecutor> getJVMExecutors();
    IExecutor[] getJVMExecutorsFromName(String processName);

    Optional<IExecutor> tryToGetJVMExecutor(String processName);

    Optional<IService> tryToGetService(String serviceName);

    Optional<IService> tryToGetService(String processName, int id);
    Collection<IExecutor> getServersExecutors();
    Collection<IExecutor> getProxiesExecutors();

    public enum JVMType {
        SERVER, PROXY
    }

    public IExecutor initIfPossible(String pathName, String name, boolean updateFile, BundleData bundleData);

    public void stop(String name,String pathName);
}
