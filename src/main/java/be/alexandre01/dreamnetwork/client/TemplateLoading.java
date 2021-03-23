package be.alexandre01.dreamnetwork.client;



import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import sun.security.util.ArrayUtil;

import java.io.File;

public class TemplateLoading {
    File[] serverDirectories = new File(Config.getPath("template/server/")).listFiles(File::isDirectory);
    File[] proxyDirectories = new File(Config.getPath("template/proxy/")).listFiles(File::isDirectory);
    public TemplateLoading(){
        System.out.println(Chalk.on("Loading templates...").underline());
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Config.createDir("template");
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

    private void loadTemplate(File[] directory, String pathName){
        if(directory != null){
            for(File dir : directory){
                //TRY TO LOAD COMPONENT
                String name = dir.getName();
                JVMExecutor jvmExecutor = JVMExecutor.initIfPossible(pathName,name,true);
                if(jvmExecutor == null){
                    notConfigured(dir);
                    continue;
                }
                if(jvmExecutor.isConfig() && jvmExecutor.hasExecutable()){
                    Console.print(Chalk.on("[✓] Template "+ dir.getName()+" loaded !").green());
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
        Console.print(Chalk.on("[!] Template "+ dir.getName()+" is not yet configured !").red());
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
