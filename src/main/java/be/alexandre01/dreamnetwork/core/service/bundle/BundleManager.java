package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BundleManager {
    @Getter private final HashMap<String, BundleData> bundleDatas = new HashMap<>();

    @Getter ArrayList<String> paths = new ArrayList<>();


    @Getter
    private final File bundleIndexFile = new File(System.getProperty("user.dir")+"/data/ServiceBundles.json");
    //@Getter
    //private BundleIndex bundleIndex = null;


    public BundleManager() {

    }

    public BundleData getBundleData(String name){
        return bundleDatas.get(name.toLowerCase());
    }
    public void addBundleData(BundleData bundleData){
        if(bundleDatas.containsKey(bundleData.getName().toLowerCase())){
            int i = 1;
            while(bundleDatas.containsKey(bundleData.getName().toLowerCase()+"_"+i)){
                i++;
            }
            bundleData.setName(bundleData.getName()+"_"+i);
        }
        bundleDatas.put(bundleData.getName().toLowerCase(), bundleData);
    }

    public void addPath(String path){
        if(!paths.contains(path)){
            paths.add(path);
        }
    }

    public void init(){

        if(!bundleIndexFile.exists()){
            try {
                bundleIndexFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(bundleIndexFile.getAbsolutePath()));


            if(Main.getBundlesLoading().isFirstLoad()){
                Console.printLang("service.bundle.manager.noBundleFound");


                //Creating bundle dir
                Config.createDir("bundles/main",false);
                Config.createDir("bundles/proxies",false);
                File mainFile,proxyFile;
                mainFile = new File("bundles/main/this-info.yml");
                proxyFile = new File("bundles/proxies/this-info.yml");
                BundleInfo main = new BundleInfo("main", JVMContainer.JVMType.SERVER);
                BundleInfo.updateFile(mainFile,main);
                BundleData mainBundle = new BundleData("main",main);


                BundleInfo proxy = new BundleInfo("proxies", JVMContainer.JVMType.PROXY);
                BundleInfo.updateFile(proxyFile,proxy);
                BundleData proxyBundle = new BundleData("proxies",proxy);

                addBundleData(mainBundle);
                addBundleData(proxyBundle);
                addPath("main/");
                addPath("proxies/");
                CustomType.reloadAll(BundlePathsNode.class);
                CustomType.reloadAll(BundlesNode.class);
            }
/*            if(bundleIndex.isEmpty()){
                Console.print(Colors.RED+"No bundle found !"+Colors.RESET);
                bundleIndex.put("proxies", new BundleData(JVMContainer.JVMType.PROXY,"proxies",true).hashToString());
                bundleIndex.put("main", new BundleData(JVMContainer.JVMType.SERVER,"main",true).hashToString());
                bundleIndex.refreshFile();
            }*/




        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    private void startAllServices(JVMContainer.JVMType... required){
        getBundleDatas().values().forEach(bundleData -> {
            if(Arrays.asList(required).contains(bundleData.getJvmType())){
                if(!bundleData.isAutoStart())
                    return;
                for(BService bService : bundleData.getServices()){
                    IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(bService.getServiceName(),bundleData);
                    if(jvmExecutor == null){
                        Console.debugPrint(Console.getFromLang("service.bundle.manager.cantFindService", bundleData.getName(), bService.getServiceName()));
                        continue;
                    }
                    for (int i = 0; i < bService.getTotalCount(); i++) {
                        jvmExecutor.startServer();
                    }
                }
            }
        });
    }

    public void onReady(){
        startAllServices(JVMContainer.JVMType.PROXY);
    }
    public void onProxyStarted(){
        startAllServices(JVMContainer.JVMType.SERVER);
    }
}
