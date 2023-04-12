package be.alexandre01.dreamnetwork.core;



import be.alexandre01.dreamnetwork.api.connection.request.CustomRequestInfo;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.connection.request.RequestFile;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import lombok.Getter;

import java.io.*;

public class BundlesLoading {
    File[] directories;
    File[] serverDirectories;
    File[] proxyDirectories;

    @Getter private boolean firstLoad = false;
    public BundlesLoading(){
        Main.setBundlesLoading(this);
        File file = new File("bundles");
        if(!file.exists()){
            firstLoad = true;
            file.mkdir();
        }
        directories = new File(Config.getPath("bundles/")).listFiles(File::isDirectory);
        serverDirectories = new File(Config.getPath("bundles/server/")).listFiles(File::isDirectory);
        proxyDirectories = new File(Config.getPath("bundles/proxy/")).listFiles(File::isDirectory);
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


        Core.getInstance().init();
    }

    public void loadBundle(File[] directories,String prefix,BundleData currentBundle){
        if(prefix.length() != 0) Main.getBundleManager().addPath(prefix);
        for(File file : directories){

            System.out.println(Console.getFromLang("bundle.loading", prefix, file.getName()));
            //Yaml yaml = new Yaml(new Constructor(BundleInfo.class));
            File bundleFile = new File(Config.getPath(file.getAbsolutePath()+"/this-info.yml"));

            BundleInfo bundleInfo;

            if(!bundleFile.exists()){
                loadTemplate(new File[]{file},currentBundle.getName(),currentBundle);
                continue;
            }

            bundleInfo = BundleInfo.loadFile(bundleFile);




            if(bundleInfo == null){
                continue;
            }
            if(bundleInfo.getServices() != null){
                //System.out.println(bundleFileInfo.getServices());
                if(bundleInfo.getServices().size() > 0){
                   // System.out.println( bundleFileInfo.getServices().get(0).getServiceName());
                }
            }


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
    private void replaceFile(InputStream in,String path,String fileName){

        try {
            assert in != null;
            Config.createDir(path,false);
            System.out.println(Console.getFromLang("bundle.replaceFile.writing", path, fileName));
            Config.write(in,new File(System.getProperty("user.dir")+Config.getPath(path+"/"+fileName)));
        } catch (IOException e) {
            e.printStackTrace();
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
                if(Config.contains(dir.getAbsolutePath()+"/"+name+"/plugins")){
                    if(isPreview){
                        System.out.println(Console.getFromLang("bundle.loadTemplate.addJar", name));
                        try {
                            is = new FileInputStream(previewFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else {
                        is = getClass().getClassLoader().getResourceAsStream("files/universal/DreamNetwork-Plugin.jar");
                    }
                    File file = new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/DreamNetwork-Plugin.jar");
                        file.delete();
                        replaceFile(is,"/bundles/"+pathName+"/"+name+"/plugins/","DreamNetwork-Plugin.jar");
                }

                IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().initIfPossible(pathName,name,false,bundleData);
                if(jvmExecutor == null){
                    System.out.println(Console.getFromLang("bundle.loadTemplate.nullJVM"));
                    notConfigured(dir);
                    continue;
                }
                System.out.println(Console.getFromLang("bundle.loadTemplate.isJVMConfig", jvmExecutor.isConfig()));
                System.out.println(Console.getFromLang("bundle.loadTemplate.hasJVMExecutable", jvmExecutor.hasExecutable()));
                if(jvmExecutor.isConfig() && jvmExecutor.hasExecutable()){
                    Console.debugPrint(Console.getFromLang("bundle.loadTemplate.loaded", dir.getName()));
                    //Utils.templates.add(dir.getName()); <- add after
                    try {
                        Thread.sleep(250);
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
        createCustomRequestsFile(proxyDirectories,"proxy");
        createCustomRequestsFile(serverDirectories,"server");
        Console.printLang("bundle.customRequest.fileCreated");
    }

    private void createCustomRequestsFile(File[] directory,String pathName){
        if(directory != null) {
            RequestFile requestFile = new RequestFile();
            for (CustomRequestInfo requestInfo : RequestType.customRequests){
                requestFile.put(requestInfo);
            }
            requestFile.encode();

            for (File dir : directory) {
                String name = dir.getName();
                //TRY TO LOAD COMPONENT
                if (Config.contains(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/plugins")) {
                    Config.createDir(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/plugins/DreamNetwork");
                    try {
                        requestFile.write(Config.getPath(System.getProperty("user.dir") + "/bundles/"+pathName+"/"+name+"/plugins/DreamNetwork"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void sendCustomsFileToProxies(InputStream in,String fileName){
        createCustomFiles(proxyDirectories,"proxy",in,fileName);
    }
    public void sendCustomsFileToServers(InputStream in,String fileName){
        createCustomFiles(serverDirectories,"server",in,fileName);
    }
    private void createCustomFiles(File[] directory,String pathName,InputStream in,String fileName){
        if(directory != null) {
            try {
            byte[] bytes = cloneInputStream(in);
            for (File dir : directory) {
                String name = dir.getName();
                //TRY TO LOAD COMPONENT
                if (Config.contains(System.getProperty("user.dir") + "/bundles/" + pathName + "/" + name + "/plugins")) {
                    File file = new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/"+fileName);
                    file.delete();
                    InputStream is = new ByteArrayInputStream(bytes);
                    replaceFile(is,"/bundles/"+pathName+"/"+name+"/plugins/",fileName);
                }

            }
            } catch (IOException e) {
                throw new RuntimeException(e);
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


    //clone inputstream without closing it
    private byte[] cloneInputStream(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        return out.toByteArray();
    }
}
