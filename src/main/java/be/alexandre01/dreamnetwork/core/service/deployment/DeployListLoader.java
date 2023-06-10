package be.alexandre01.dreamnetwork.core.service.deployment;

import be.alexandre01.dreamnetwork.core.console.colors.Colors;

import java.io.File;

public class DeployListLoader {
    public DeployListLoader() {
        System.out.println(Colors.YELLOW_BOLD+"Loading deployment folder...");

        File file = new File("deploys");
        if (!file.exists()) {
            file.mkdir();
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        if(files.length == 0){
            System.out.println("No deploy found... skipping");
            return;
        }
        for (File dir : files) {
            DeployContainer deployContainer = new DeployContainer(dir);
            if (deployContainer.load()) {
                System.out.println(Colors.CYAN_BRIGHT+ "Loaded deploy: " + deployContainer.getDeployData().getName());
                //DeployList.addDeployContainer(deployContainer);

            }else {
                System.out.println(Colors.RED+"Failed to load deploy: " + deployContainer.getName());
            }
        }
    }
}
