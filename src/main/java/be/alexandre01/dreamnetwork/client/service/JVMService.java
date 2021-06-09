package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.client.service.interfaces.IService;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class JVMService implements IService {
    private int id;
    private int port;
    private JVMExecutor jvmExecutor;
    private Process process;

    @Override
    public void stop(){

    }

    @Override
    public void restart() {

    }

    @Override
    public void sendData() {

    }

    @Override
    public void kill() {
        process.destroy();
        process.destroyForcibly();
        System.out.println("DESTROY");
    }
}
