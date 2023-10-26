package be.alexandre01.dreamnetwork.core.service.deployment;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 26/10/2023 at 10:29
*/

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.config.IFileCopyAsync;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.config.FileCopyAsync;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class StaticUpdater {


    public StaticUpdater(){
    }


    public CompletableFuture<Boolean> update(File from, File to){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if(!from.exists() || !to.exists()){
            return CompletableFuture.completedFuture(false);
        }

        // copy
        FileCopyAsync fileCopyAsync = new FileCopyAsync();
        fileCopyAsync.setOverrideIfChanged(true);

        fileCopyAsync.execute(from.toPath(), to.toPath(), new IFileCopyAsync.ICallback() {
            @Override
            public void call() {
                System.out.println("Copied "+from.getName()+" to "+to.getName());
                completableFuture.complete(true);
            }

            @Override
            public void cancel() {
                System.out.println("Failed to copy "+from.getName()+" to "+to.getName());
                completableFuture.complete(false);
            }
        },false,"deploy.yml");
        return completableFuture;
    }



}
