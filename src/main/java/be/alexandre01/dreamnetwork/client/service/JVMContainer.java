package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.client.config.Config;
import lombok.Getter;

import java.util.HashMap;

public class JVMContainer implements IContainer {
    public volatile HashMap<String, IJVMExecutor> jvmExecutorsServers = new HashMap<>();
    public volatile HashMap<String, IJVMExecutor> jvmExecutorsProxy = new HashMap<>();


    @Override
    public synchronized IJVMExecutor getJVMExecutor(String processName, JVMType jvmType){
        switch (jvmType){
            case SERVER:
                return jvmExecutorsServers.get(processName);
            case PROXY:
                return jvmExecutorsProxy.get(processName);
        }
        return null;
    }


    @Override
    public IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile) {
        IJVMExecutor.Mods type = null;

        String xms = null;
        String xmx = null;
        int port = 0;
        boolean proxy = false;

        try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir") + "/template/" + pathName + "/" + name + "/network.yml")) {
                if (line.startsWith("type:")) {
                    type = IJVMExecutor.Mods.valueOf(line.replace("type:", "").replaceAll(" ", ""));
                }
                if (line.startsWith("xms:")) {
                    xms = line.replace("xms:", "").replaceAll(" ", "");
                }
                if (line.startsWith("xmx:")) {
                    xmx = line.replace("xmx:", "").replaceAll(" ", "");
                }
                if (line.startsWith("port:")) {
                    port = Integer.parseInt(line.replace("port:", "").replaceAll(" ", ""));
                }
                if (line.contains("proxy: true")) {
                    proxy = true;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return (IJVMExecutor) new JVMExecutor(pathName, name, type, xms, xmx, port, proxy, updateFile);
    }

    @Override
    public void stop(String name, String pathName) {
        String finalName = name.split("-")[0];
        /*if(getProcess(name) != null){
            System.out.println("DESTROY");
            getProcess(name).destroy();
        }*/
        if (IJVMExecutor.getStartServerList().contains(name)) {
            IJVMExecutor.getStartServerList().remove(name);

        }
        if (IJVMExecutor.getServersPort().containsKey(name)) {
            int port = IJVMExecutor.getServersPort().get(name);
            IJVMExecutor.getServersPort().put("cache-" + IJVMExecutor.getCache(), port);
            IJVMExecutor.getServersPort().remove(name);
        }


        if (Config.contains(Config.getPath(System.getProperty("user.dir") + "/tmp/" + pathName + "/" + finalName + "/" + name))) {
            Config.removeDir(Config.getPath(System.getProperty("user.dir") + "/tmp/" + pathName + "/" + finalName + "/" + name));
        }
    }

    @Override
    public HashMap<String, IJVMExecutor> getJVMExecutorsServers() {
        return jvmExecutorsServers;
    }

    @Override
    public HashMap<String, IJVMExecutor> getJVMExecutorsProxy() {
        return jvmExecutorsProxy;
    }


    public synchronized void addExecutor(JVMExecutor jvmExecutor, JVMType jvmType){
        switch (jvmType){
            case SERVER:
                jvmExecutorsServers.put(jvmExecutor.getName(),jvmExecutor);
                break;
            case PROXY:
                jvmExecutorsProxy.put(jvmExecutor.getName(),jvmExecutor);
                break;
        }

    }

}
