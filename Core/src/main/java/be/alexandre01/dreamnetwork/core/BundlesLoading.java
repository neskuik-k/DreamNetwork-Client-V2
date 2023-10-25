package be.alexandre01.dreamnetwork.core;



import be.alexandre01.dreamnetwork.api.connection.core.request.CustomRequestInfo;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.FileDispatcher;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.connection.core.requests.RequestFile;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;

import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import lombok.Getter;

import java.io.*;
import java.util.stream.Collectors;

public class BundlesLoading {
    File[] directories;

    @Getter FileDispatcher fileDispatcher = new FileDispatcher();

    @Getter private boolean firstLoad = false;
    public BundlesLoading(){
        Main.setBundlesLoading(this);
        File file = new File("bundles");
        if(!file.exists()){
            System.out.println("First load");
            firstLoad = true;
            file.mkdir();
        }
        directories = new File(Config.getPath("bundles/")).listFiles(File::isDirectory);
        System.out.println(Console.getFromLang("bundles.loading")+ Colors.RESET);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       loadBundle(directories,"",null);
       // loadTemplate(proxyDirectories,"proxy");
        // loadTemplate(serverDirectories,"server");


        System.out.println("\n");



    }

    public void loadBundle(File[] directories, String prefix, BundleData currentBundle){
        if(prefix.length() != 0) Main.getBundleManager().addPath(prefix);
        for(File file : directories){


            //Yaml yaml = new Yaml(new Constructor(BundleInfo.class));
            File bundleFile = new File(Config.getPath(file.getAbsolutePath()+"/this-info.yml"));
            BundleInfo bundleInfo;
            if(!bundleFile.exists()){
                loadTemplate(new File[]{file},currentBundle.getName(),currentBundle);
                continue;
            }
            System.out.println(Console.getFromLang("bundle.loading", prefix,file.getName()));
            bundleInfo = BundleInfo.loadFile(bundleFile);

            if(bundleInfo == null){
                continue;
            }
          //  System.out.println("Le nom en question > "+bundleInfo.name);
            BundleData bundleData = new BundleData(bundleInfo.name,bundleInfo);

            Main.getBundleManager().addBundleData(bundleData);
            BundleInfo main;
            File[] servers = file.listFiles(File::isDirectory);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadBundle(servers,file.getName()+"/",bundleData);
            //loadTemplate(servers,file.getName());
        }
    }

    private void loadTemplate(File[] directory, String pathName, BundleData bundleData){
        File previewFile = new File(Config.getPath("preview/DreamNetwork-Plugin.jar"));
        InputStream is = null;
        boolean isPreview = false;
        if(previewFile.exists())
            isPreview = true;

        if(directory != null){
            for(File dir : directory){
                String name = dir.getName();
                //TRY TO LOAD COMPONENT
                if(Config.contains(dir.getAbsolutePath()+"/plugins")){

                    if(isPreview){
                        System.out.println(Console.getFromLang("bundle.loadTemplate.addJar", name));
                        try {
                            System.out.println("");
                            is = new FileInputStream(previewFile);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else {
                        is = getClass().getClassLoader().getResourceAsStream("files/universal/DreamNetwork-Plugin.jar");
                    }
                    File file = new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/DreamNetwork-Plugin.jar");
                        file.delete();
                        fileDispatcher.replaceFile(is,System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/","DreamNetwork-Plugin.jar");
                }

                IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().initIfPossible(pathName,name,false,bundleData);
                if(jvmExecutor == null){
                    notConfigured(dir);
                    continue;
                }
                if(jvmExecutor.isConfig() && jvmExecutor.hasExecutable()){
                    Console.debugPrint(Console.getFromLang("bundle.loadTemplate.loaded", dir.getName()));
                    //Utils.templates.add(dir.getName()); <- add after
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    notConfigured(dir);
                }
            }
        }
    }

    public void createCustomRequestsFile(){
        File[] servers = Core.getInstance().getJvmContainer().getServersExecutors().stream().map(IJVMExecutor::getFileRootDir).collect(Collectors.toList()).toArray(new File[0]);
        File[] proxies =  Core.getInstance().getJvmContainer().getServersExecutors().stream().map(IJVMExecutor::getFileRootDir).collect(Collectors.toList()).toArray(new File[0]);
        createCustomRequestsFile(servers);
        createCustomRequestsFile(proxies);
        Console.printLang("bundle.customRequest.fileCreated");
    }

    private void createCustomRequestsFile(File[] directory){
        if(directory != null) {
            RequestFile requestFile = new RequestFile();
            for (CustomRequestInfo requestInfo : RequestType.customRequests){
                requestFile.put(requestInfo);
            }
            requestFile.encode();

            for (File dir : directory) {
                //TRY TO LOAD COMPONENT
                if (Config.contains( dir.getAbsolutePath() + "/plugins")) {
                    Config.createDir( dir.getAbsolutePath()+ "/plugins/DreamNetwork",false);
                    try {
                        requestFile.write(Config.getPath(dir.getAbsolutePath()+"/plugins/DreamNetwork"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }





    private void notConfigured(File dir){
        Console.debugPrint(Console.getFromLang("bundle.notConfigured", dir.getName()));
        try {
            Thread.sleep(150);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
