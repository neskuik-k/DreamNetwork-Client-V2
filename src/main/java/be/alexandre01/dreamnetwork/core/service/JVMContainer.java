package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JVMContainer implements IContainer {
    public volatile ArrayList<IJVMExecutor> jvmExecutors = new ArrayList<>();


    public Collection<IJVMExecutor> getServersExecutors() {
        return jvmExecutors.stream().filter(ijvmExecutor -> !(ijvmExecutor.isProxy())).collect(Collectors.toList());
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
    public IJVMExecutor[] getJVMExecutorsFromName(String processName) {
        return jvmExecutors.stream().filter(ijvmExecutor -> ijvmExecutor.getName().equals(processName)).toArray(IJVMExecutor[]::new);
    }

    @Override
    public IJVMExecutor tryToGetJVMExecutor(String processName) {
        try {
            if(processName.contains("/")){
                String[] split = processName.split("/");
                StringBuilder bundle = new StringBuilder();
                for(int i = 0; i < split.length-1; i++){
                    bundle.append(split[i]);
                }
                return getJVMExecutor(split[split.length-1],bundle.toString());
            }
            IJVMExecutor[] jvmExecutors = getJVMExecutorsFromName(processName);
            if(jvmExecutors.length == 0){
                return null;
            }

            if(jvmExecutors.length > 1){
                System.out.println(Colors.RED+ "Can't identifiate JVMExecutor found for the name: "+processName+" please use the full path like:");
                for(IJVMExecutor jvmExecutor : jvmExecutors){
                    System.out.println(Colors.YELLOW+ "> "+jvmExecutor.getFullName());
                }
                return null;
            }
            return jvmExecutors[0];
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public IService tryToGetService(String serviceName){
        String[] split = serviceName.split("-");
        int id;
        try {
            id = Integer.parseInt(split[1]);
        }catch (Exception e){
            System.out.println(Colors.RED+"The text -> "+ split[1]+ " is not a number");
            return null;
        }
        return tryToGetService(split[0],id);
    }
    @Override
    public IService tryToGetService(String processName, int id){
        IJVMExecutor jvmExecutor = tryToGetJVMExecutor(processName);
        if(jvmExecutor == null){
            return null;
        }
        return jvmExecutor.getService(id);
    }


    @Override
    public ArrayList<IJVMExecutor> getJVMExecutors() {
        return jvmExecutors;
    }


    @Override
    public IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile,BundleData bundleData) {
     //   System.out.println(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/network.yml");
        try {
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
