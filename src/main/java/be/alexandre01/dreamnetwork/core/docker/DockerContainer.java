package be.alexandre01.dreamnetwork.core.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;


public class DockerContainer {
    @SneakyThrows
    public static void main(String[] args){
        DockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .withDockerConfig("/home/alexandre/.docker")
                .withDockerCertPath("/home/alexandre/.docker/certs")
                .build();



        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(custom.getDockerHost())
                .sslConfig(custom.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try (DockerHttpClient.Response response = httpClient.execute(request)) {
            System.out.println(response.getStatusCode());
            try {
                System.out.println(IOUtils.toString(response.getBody()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //assertThat(response.getStatusCode(), equalTo(200));
            //assertThat(IOUtils.toString(response.getBody()), equalTo("OK"));
        }
        DockerClient dockerClient = DockerClientImpl.getInstance(custom,httpClient);
        dockerClient.pingCmd().exec();
        Info info = dockerClient.infoCmd().exec();
        System.out.println(info.getArchitecture());
        System.out.println(info.getOsType());
        System.out.println(info.getContainers());
        System.out.println(info.getContainersRunning());
        System.out.println(info.getContainersPaused());
        System.out.println(info.getContainersStopped());
        System.out.println(info.getImages());



        System.out.println(dockerClient.pullImageCmd("nimmis/spigot:latest").exec(new PullImageResultCallback()).awaitCompletion());

        boolean isBuild = false;
        Container container = null;

        List<Container> d = dockerClient.listContainersCmd().withShowAll(true).exec();
        for(Container c : d){
            for (String name : c.getNames()) {
                System.out.println(name);
                if(name.equals("/spigot")){
                    System.out.println("YES ");
                    isBuild = true;
                    container = c;
                }
            }
        }

        if(!isBuild){
            CreateContainerResponse createContainerResponse
                    = dockerClient.createContainerCmd("nimmis/spigot:latest")
                    .withName("spigot")
                    .withHostName("Alexandre")
                    .withEnv("EULA=TRUE","SPIGOT_VER=1.8")
                    .exec();
            dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        }else {
            System.out.println(container.getState());
            if(container.getState().equals("exited")){
                dockerClient.startContainerCmd(container.getId()).exec();
            }
        }



    }
}
