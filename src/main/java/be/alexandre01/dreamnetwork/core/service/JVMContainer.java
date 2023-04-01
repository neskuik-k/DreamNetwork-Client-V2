package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.commands.sub.types.AllServersNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ProxiesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ServersNode;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JVMContainer implements IContainer {
    public volatile ArrayList<IJVMExecutor> jvmExecutors = new ArrayList<>();


    public Collection<IJVMExecutor> getServersExecutors() {
        return jvmExecutors.stream().filter(ijvmExecutor -> !ijvmExecutor.isProxy()).collect(Collectors.toList());
    }

    public Collection<IJVMExecutor> getProxiesExecutors() {
        return jvmExecutors.stream().filter(IJVMExecutor::isProxy).collect(Collectors.toList());
    }


    @Override
    public synchronized IJVMExecutor getJVMExecutor(String processName, BundleData bundleData) {
        return bundleData.getExecutors().get(processName);
    }

    @Override
    public synchronized IJVMExecutor getJVMExecutor(String processName, String bundleData) throws NullPointerException{
        //System.out.println("getJVMExecutor");
        //System.out.println(Main.getBundleManager().getBundleDatas().keySet());
        return Main.getBundleManager().getBundleDatas().get(bundleData).getExecutors().get(processName);
    }


    @Override
    public IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile,BundleData bundleData) {
        IJVMExecutor.Mods type = null;

        String xms = null;
        String xmx = null;
        int port = 0;
        boolean proxy = false;
     //   System.out.println(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/network.yml");

        /*try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/network.yml")) {
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
        }*/

        try {
            System.out.println("INIT");
            return new JVMExecutor(pathName, name, bundleData);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void stop(String name, String pathName) {
        String finalName = name.split("-")[0];
        /*if(getProcess(name) != null){
            System.out.println("DESTROY");
            getProcess(name).destroy();
        }*/
        IJVMExecutor.getStartServerList().remove(name);
        if (IJVMExecutor.getServersPort().containsKey(name)) {
            int port = IJVMExecutor.getServersPort().get(name);
            IJVMExecutor.getServersPort().put("cache-" + IJVMExecutor.getCache(), port);
            IJVMExecutor.getServersPort().remove(name);
        }


        if (Config.contains(Config.getPath(System.getProperty("user.dir") + "/runtimes/" + pathName + "/" + finalName + "/" + name))) {
            Config.removeDir(Config.getPath(System.getProperty("user.dir") + "/runtimes/" + pathName + "/" + finalName + "/" + name));
        }
    }




    public synchronized void addExecutor(JVMExecutor jvmExecutor, BundleData bundleData) {
        jvmExecutors.add(jvmExecutor);
        bundleData.getExecutors().put(jvmExecutor.getName(), jvmExecutor);
    }

}
