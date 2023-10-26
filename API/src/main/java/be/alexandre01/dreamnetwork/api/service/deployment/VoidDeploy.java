package be.alexandre01.dreamnetwork.api.service.deployment;

import java.io.File;

public class VoidDeploy implements Deploy{

    private final File file;
    private final DeployData.DeployType[] deployTypes;

    public VoidDeploy(File directory, DeployData.DeployType[] deployTypes){
        this.file = directory;

        this.deployTypes = deployTypes;
    }

    @Override
    public File getDirectory() {
        return file;
    }

    @Override
    public DeployData.DeployType[] getDeployTypes() {
        return deployTypes;
    }
}
