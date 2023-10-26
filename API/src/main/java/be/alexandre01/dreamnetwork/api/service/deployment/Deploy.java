package be.alexandre01.dreamnetwork.api.service.deployment;

import be.alexandre01.dreamnetwork.api.service.deployment.DeployData;

import java.io.File;

public interface Deploy {
    public File getDirectory();

    public DeployData.DeployType[] getDeployTypes();
}
