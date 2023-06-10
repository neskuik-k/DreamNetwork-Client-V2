package be.alexandre01.dreamnetwork.core.service.deployment;

import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.config.CopyAndPaste;
import be.alexandre01.dreamnetwork.core.config.EstablishedAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Deployer {
    List<DeployData> deployDatas = new ArrayList<>();

    int tasks = 0;

    public Deployer(){

    }

    public void addDeployData(DeployData deployData){
        this.deployDatas.add(deployData);
    }

    public void deploys(File folder,DeployAction action) throws IOException {
        DeployData deployData = deployDatas.get(tasks);
        Config.asyncCopy(deployData.getDirectory(), folder, new EstablishedAction() {
            @Override
            public void completed() {
                tasks++;
                if(tasks == deployDatas.size()){
                    action.completed();
                    return;
                }
                try {
                    deploys(folder,action);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void cancelled() {
                action.cancelled();
                return;
            }
        });
    }
    interface DeployAction{
        void completed();
        void cancelled();
    }
}
