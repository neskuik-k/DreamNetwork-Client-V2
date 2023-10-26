package be.alexandre01.dreamnetwork.core.service.deployment;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.deployment.Deploy;
import be.alexandre01.dreamnetwork.core.config.FileCopyAsync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deployer {
    List<Deploy> deployDatas = new ArrayList<>();

    int tasks = 0;

    public Deployer(){

    }
    public void addDeploy(Deploy deploy){
        if(!deployDatas.contains(deploy))
            this.deployDatas.add(deploy);
    }
    public void deploys(File folder,DeployAction action,String... exceptFile) throws IOException {
        //add /deploy.yml in exceptFile
        exceptFile = Arrays.copyOf(exceptFile, exceptFile.length + 1);
        exceptFile[exceptFile.length - 1] = "deploy.yml";
        Deploy deploy = deployDatas.get(0);
        Console.fine("Deploying "+deploy.getDirectory().getName() + "in async mode");
       // System.out.println(deploy.getDirectory().getName());
        Config.asyncCopy(deploy.getDirectory(), folder, new FileCopyAsync.ICallback() {
            @Override
            public void call() {
               // System.out.println("Task completed "+tasks);
              //  Console.debugPrint("Task completed "+tasks);
                deployDatas.remove(0);
                if(deployDatas.isEmpty()){
                    action.completed();
                    return;
                }
                try {
                    deploys(folder,action);
                } catch (IOException e) {
                    action.cancelled();
                    Console.bug(e);
                }
            }

            @Override
            public void cancel() {
                action.cancelled();
            }
        },false,exceptFile);
    }
    public interface DeployAction{
        void completed();
        void cancelled();
    }
}
