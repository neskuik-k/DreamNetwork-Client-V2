package be.alexandre01.dreamnetwork.api.service;


import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface IContainer {
    IExecutor getExecutor(String processName, BundleData bundleData);

    IExecutor getExecutor(String processName, String bundleName) throws NullPointerException;

    ArrayList<IExecutor> getExecutors();
    IExecutor[] getExecutorsFromName(String processName);

    Optional<IExecutor> findExecutor(String processName);

    Optional<IService> findService(String serviceName);

    Optional<IService> findService(String processName, int id);
    Collection<IExecutor> getServersExecutors();
    Collection<IExecutor> getProxiesExecutors();

    public enum JVMType {
        SERVER, PROXY
    }

    //  public IExecutor initIfPossible(String pathName, String name, boolean updateFile, BundleData bundleData);

    public void stop(String name,String pathName);
}
