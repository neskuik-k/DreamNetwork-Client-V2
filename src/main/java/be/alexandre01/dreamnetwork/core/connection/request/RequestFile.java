package be.alexandre01.dreamnetwork.core.connection.request;

import be.alexandre01.dreamnetwork.api.connection.request.CustomRequestInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestFile {
    String s = "";
    String encoded;


    public void put(CustomRequestInfo requestInfo) {
        s += "|" + requestInfo.id() + ":" + requestInfo.getCustomName();
    }

    public void encode(){
        encoded = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    public void write(String path) throws IOException {
        File file = new File(path +"/requests.dream");

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(encoded);
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
