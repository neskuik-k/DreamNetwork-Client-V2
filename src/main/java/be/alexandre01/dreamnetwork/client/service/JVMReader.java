package be.alexandre01.dreamnetwork.client.service;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Data @Builder
public class JVMReader extends Thread{
    JVMService jvmService;


    @SneakyThrows
    @Override
    public void run() {
        Process process = jvmService.getProcess();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null){
            System.out.println(line);
        }

        try {
            int exitValue = process.waitFor();
            //set status of you button as process is stop or do call function
            jvmService.getJvmExecutor().removeService(jvmService.getId());
            System.out.println("\n\nExit Value from "+jvmService.getJvmExecutor().getName()+" is " + exitValue);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
