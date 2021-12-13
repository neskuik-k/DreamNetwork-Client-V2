package be.alexandre01.dreamnetwork.client.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;

public class DockerContainer {
    public static void main(String[] args){
        DefaultDockerClientConfig config
                = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerClient dockerClient = DockerClientImpl
                .getInstance(config);
    }
}
