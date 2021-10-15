package be.alexandre01.dreamnetwork.client.service.bundle;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaIndex;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaVersion;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class BundleManager {
    @Getter
    private final File bundleIndexFile = new File(System.getProperty("user.dir")+"/data/ServiceBundles.json");
    @Getter
    private BundleIndex bundleIndex = null;
    @Getter
    private final ArrayList<BundleData> bundleDatas;

    public BundleManager() {
        this.bundleDatas = new ArrayList<>();
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
            this.bundleIndex = gson.fromJson(reader,BundleIndex.class);
            if(bundleIndex == null)
                this.bundleIndex = new BundleIndex();
            bundleIndex.setIndexFile(bundleIndexFile);

            if(bundleIndex.isEmpty()){
                bundleIndex.put("defaultProxy", new BundleData(JVMContainer.JVMType.PROXY,"defaultProxy",true).hashToString());
                bundleIndex.put("defaultServer", new BundleData(JVMContainer.JVMType.SERVER,"defaultServer",true).hashToString());
                bundleIndex.refreshFile();
            }

            if(!bundleDatas.isEmpty()){
                for (BundleData b : bundleDatas) {
                    Console.debugPrint(b.toString());
                }
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    private void startAllServices(JVMContainer.JVMType... required){
        getBundleDatas().forEach(bundleData -> {
            if(Arrays.asList(required).contains(bundleData.getJvmType())){
                if(!bundleData.isAutoStart())
                    return;
                for(BService bService : bundleData.getServices()){
                    JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(bService.getServiceName(),bundleData.getJvmType());
                    if(jvmExecutor == null){
                        Console.debugPrint(Colors.RED +"- Bundle "+ bundleData.getName()+" can't find "+ bService.getServiceName()+" service");
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
