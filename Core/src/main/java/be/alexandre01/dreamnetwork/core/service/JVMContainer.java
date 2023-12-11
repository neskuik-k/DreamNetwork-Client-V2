package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import lombok.Synchronized;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class JVMContainer implements IContainer {
    public final ArrayList<IExecutor> jvmExecutors = new ArrayList<>();
    public Collection<IExecutor> getServersExecutors() {
        return jvmExecutors.stream().filter(ijvmExecutor -> !(ijvmExecutor.isProxy())).collect(Collectors.toList());
    }

    public Collection<IExecutor> getProxiesExecutors() {
        return jvmExecutors.stream().filter(IExecutor::isProxy).collect(Collectors.toList());
    }


    @Override @Synchronized
    public IExecutor getExecutor(String processName, BundleData bundleData) {
        return bundleData.getExecutors().get(processName);
    }

    @Override @Synchronized
    public synchronized IExecutor getExecutor(String processName, String bundleData) throws NullPointerException{
        //System.out.println("getJVMExecutor");
        //System.out.println(Main.getBundleManager().getBundleDatas().keySet());
        return Main.getBundleManager().getBundleDatas().get(bundleData).getExecutors().get(processName);
    }



    @Override
    public IExecutor[] getExecutorsFromName(String processName) {
        return jvmExecutors.stream().filter(ijvmExecutor -> {
            if(ijvmExecutor.getCustomName().isPresent()) {
                return ijvmExecutor.getCustomName().get().equals(processName);
            }else {
                return ijvmExecutor.getName().equals(processName);
            }
        }).toArray(IExecutor[]::new);
    }

    @Override
    public Optional<IExecutor> findExecutor(String processName) {
        return Optional.empty().map(object -> {
            if(processName.contains("/")){
                String[] split = processName.split("/");
                StringBuilder bundle = new StringBuilder();
                for(int i = 0; i < split.length-1; i++){
                    bundle.append(split[i]);
                }
                try {
                    return getExecutor(split[split.length - 1], bundle.toString());
                }catch (NullPointerException e){
                    System.out.println(Colors.RED+"Can't find the bundle: "+bundle.toString());
                    return null;
                }
            }
            IExecutor[] jvmExecutors = getExecutorsFromName(processName);
            if(jvmExecutors.length == 0){
                return null;
            }

            if(jvmExecutors.length > 1){
                System.out.println(Colors.RED+ "Can't identifiate JVMExecutor found for the name: "+processName+" please use the full path like:");
                for(IExecutor jvmExecutor : jvmExecutors){
                    System.out.println(Colors.YELLOW+ "> "+jvmExecutor.getFullName());
                }
                return null;
            }
            return jvmExecutors[0];
        });
    }

    @Override
    public Optional<IService> findService(String serviceName){
        String[] split = serviceName.split("-");
        int id;
        try {
            id = Integer.parseInt(split[1]);
        }catch (Exception e){
            System.out.println(Colors.RED+"The id is not a number");
            return Optional.empty();
        }
        return findService(split[0],id);
    }
    @Override
    public Optional<IService> findService(String processName, int id){
        return findExecutor(processName)
                .flatMap(iExecutor -> Optional.ofNullable(iExecutor.getService(id)));
    }


    @Override
    public ArrayList<IExecutor> getExecutors() {
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
