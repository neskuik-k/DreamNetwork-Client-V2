package be.alexandre01.dreamnetwork.core.service.deployment;

import java.io.File;

public interface Deploy {
    public File getDirectory();

    public DeployData.DeployType[] getDeployTypes();
}
