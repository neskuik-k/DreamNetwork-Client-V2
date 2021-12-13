package be.alexandre01.dreamnetwork.client;



import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Level;

public class TemplateLoading {

    File[] serverDirectories = new File(Config.getPath("template/server/")).listFiles(File::isDirectory);
    File[] proxyDirectories = new File(Config.getPath("template/proxy/")).listFiles(File::isDirectory);
    public TemplateLoading(){
        System.out.println(Chalk.on("Loading templates...").underline());
        try {
            //Alexandre actuellement
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Config.createDir("template",false);
        loadTemplate(proxyDirectories,"proxy");
        loadTemplate(serverDirectories,"server");

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n");
        Client.getInstance().init();
    }
    private void replaceFile(InputStream in,String path,String fileName){

        try {
            assert in != null;
            Config.createDir(path,false);
            Config.write(in,new File(System.getProperty("user.dir")+Config.getPath(path+"/"+fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadTemplate(File[] directory, String pathName){
        File previewFile = new File(Config.getPath("preview/DreamNetwork-Plugin.jar"));
        InputStream is = null;
        boolean isPreview = false;
        if(previewFile.exists())
            isPreview = true;

        if(directory != null){
            for(File dir : directory){
                String name = dir.getName();
                //TRY TO LOAD COMPONENT
                if(Config.contains(System.getProperty("user.dir")+"/template/"+pathName+"/"+name+"/plugins")){
                    if(isPreview){
                        System.out.println("[+] Adding preview jar plugin to template "+name+"...");
                        try {
                            is = new FileInputStream(previewFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else {
                        is = getClass().getClassLoader().getResourceAsStream("files/universal/DreamNetwork-Plugin.jar");
                    }
                    File file = new File(System.getProperty("user.dir")+"/template/"+pathName+"/"+name+"/plugins/DreamNetwork-Plugin.jar");
                        file.delete();
                        replaceFile(is,"/template/"+pathName+"/"+name+"/plugins/","DreamNetwork-Plugin.jar");
                }


                JVMExecutor jvmExecutor = JVMExecutor.initIfPossible(pathName,name,false);
                if(jvmExecutor == null){
                    notConfigured(dir);
                    continue;
                }
                if(jvmExecutor.isConfig() && jvmExecutor.hasExecutable()){
                    Console.debugPrint(Chalk.on("[O] Template "+ dir.getName()+" loaded !").green());
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

    private void notConfigured(File dir){
        Console.debugPrint(Chalk.on("[!] Template "+ dir.getName()+" is not yet configured !").red());
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
