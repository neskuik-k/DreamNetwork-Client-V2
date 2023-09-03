package be.alexandre01.dreamnetwork.core.connection.external.service;

import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMProfiles;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;

import java.io.File;
import java.util.Collection;

public class VirtualExecutor implements IJVMExecutor {
    @Override
    public ExecutorCallbacks startServer() {
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(String profile) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(String profile, ExecutorCallbacks callbacks) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig, ExecutorCallbacks callbacks) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServers(int i) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServers(int i, IConfig jvmConfig) {
        return null;
    }

    @Override
    public ExecutorCallbacks startServers(int i, String profile) {
        return null;
    }

    @Override
    public void removeService(IService service) {

    }

    @Override
    public IService getService(Integer i) {
        return null;
    }

    @Override
    public Collection<IService> getServices() {
        return null;
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isConfig() {
        return false;
    }

    @Override
    public boolean isFixedData() {
        return false;
    }

    @Override
    public File getFileRootDir() {
        return null;
    }

    @Override
    public IConfig getConfig() {
        return null;
    }

    @Override
    public IStartupConfig getStartupConfig() {
        return null;
    }

    @Override
    public Mods getType() {
        return null;
    }

    @Override
    public String getXms() {
        return null;
    }

    @Override
    public String getStartup() {
        return null;
    }

    @Override
    public String getExecutable() {
        return null;
    }

    @Override
    public String getXmx() {
        return null;
    }

    @Override
    public String getPathName() {
        return null;
    }

    @Override
    public String getJavaVersion() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public boolean hasExecutable() {
        return false;
    }

    @Override
    public BundleData getBundleData() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public ExecType getExecType() {
        return null;
    }

    @Override
    public InstallationLinks getInstallLink() {
        return null;
    }

    @Override
    public JVMProfiles getJvmProfiles() {
        return null;
    }
}
