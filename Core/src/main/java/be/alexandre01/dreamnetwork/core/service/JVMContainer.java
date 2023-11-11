package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class JVMContainer implements IContainer {
    public volatile ArrayList<IExecutor> jvmExecutors = new ArrayList<>();


    public Collection<IExecutor> getServersExecutors() {
        return jvmExecutors.stream().filter(ijvmExecutor -> !(ijvmExecutor.isProxy())).collect(Collectors.toList());
    }

    public Collection<IExecutor> getProxiesExecutors() {
        return jvmExecutors.stream().filter(IExecutor::isProxy).collect(Collectors.toList());
    }


    @Override
    public synchronized IExecutor getJVMExecutor(String processName, BundleData bundleData) {
        return bundleData.getExecutors().get(processName);
    }

    @Override
    public synchronized IExecutor getJVMExecutor(String processName, String bundleData) throws NullPointerException{
        //System.out.println("getJVMExecutor");
        //System.out.println(Main.getBundleManager().getBundleDatas().keySet());
        return Main.getBundleManager().getBundleDatas().get(bundleData).getExecutors().get(processName);
    }



    @Override
    public IExecutor[] getJVMExecutorsFromName(String processName) {
        return jvmExecutors.stream().filter(ijvmExecutor -> {
            if(ijvmExecutor.getCustomName().isPresent()) {
                return ijvmExecutor.getCustomName().get().equals(processName);
            }else {
                return ijvmExecutor.getName().equals(processName);
            }
        }).toArray(IExecutor[]::new);
    }

    @Override
    public Optional<IExecutor> tryToGetJVMExecutor(String processName) {
        try {
            if(processName.contains("/")){
                String[] split = processName.split("/");
                StringBuilder bundle = new StringBuilder();
                for(int i = 0; i < split.length-1; i++){
                    bundle.append(split[i]);
                }
                return Optional.ofNullable(getJVMExecutor(split[split.length - 1], bundle.toString()));
            }
            IExecutor[] jvmExecutors = getJVMExecutorsFromName(processName);
            if(jvmExecutors.length == 0){
                return Optional.empty();
            }

            if(jvmExecutors.length > 1){
                System.out.println(Colors.RED+ "Can't identifiate JVMExecutor found for the name: "+processName+" please use the full path like:");
                for(IExecutor jvmExecutor : jvmExecutors){
                    System.out.println(Colors.YELLOW+ "> "+jvmExecutor.getFullName());
                }
                return Optional.empty();
            }
            return Optional.ofNullable(jvmExecutors[0]);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<IService> tryToGetService(String serviceName){
        String[] split = serviceName.split("-");
        int id;
        try {
            id = Integer.parseInt(split[1]);
        }catch (Exception e){
            System.out.println(Colors.RED+"The id is not a number");
            return Optional.empty();
        }
        return tryToGetService(split[0],id);
    }
    @Override
    public Optional<IService> tryToGetService(String processName, int id){
        Optional<IExecutor> jvmExecutor = tryToGetJVMExecutor(processName);
        return jvmExecutor.flatMap(ijvmExecutor -> Optional.ofNullable(ijvmExecutor.getService(id)));
    }


    @Override
    public ArrayList<IExecutor> getJVMExecutors() {
        return jvmExecutors;
    }


    @Override
    public IExecutor initIfPossible(String pathName, String name, boolean updateFile, BundleData bundleData) {
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
        JVMExecutor.getStartServerList().remove(name);
        if (JVMExecutor.getServersPort().containsKey(name)) {
            int port = JVMExecutor.getServersPort().get(name);
            JVMExecutor.getServersPort().put("cache-" + JVMExecutor.getCache(), port);
            JVMExecutor.getServersPort().remove(name);
        }


        if (Config.contains(Config.getPath(System.getProperty("user.dir") + "/runtimes/" + pathName + "/" + finalName + "/" + name))) {
            Config.removeDir(System.getProperty("user.dir") + "/runtimes/" + pathName + "/" + finalName + "/" + name);
        }
    }




    public synchronized void addExecutor(JVMExecutor jvmExecutor, BundleData bundleData) {
        jvmExecutors.add(jvmExecutor);
        bundleData.getExecutors().put(jvmExecutor.getName(), jvmExecutor);
    }


}
