package be.alexandre01.dreamnetwork.client.config.remote;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.client.service.bundle.BundleIndex;
import be.alexandre01.dreamnetwork.client.utils.json.JSONFileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class DevToolsToken {
    private final File tokenFile = new File(System.getProperty("user.dir")+"/data/DevToolsToken.json");

    public void init(){
        if(!tokenFile.exists()){
            try {
                tokenFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(tokenFile.getAbsolutePath()));
            DevToolsIndex devToolsIndex = gson.fromJson(reader, DevToolsIndex.class);
            if(devToolsIndex == null)
                devToolsIndex = new DevToolsIndex();
            devToolsIndex.setIndexFile(tokenFile);

            if(devToolsIndex.isEmpty()){
                SecureRandom random = new SecureRandom();
                byte bytes[] = new byte[255];
                random.nextBytes(bytes);
                devToolsIndex.put("activeRemote", false);
                devToolsIndex.put("token", Base64.getEncoder().encodeToString(bytes));
                devToolsIndex.refreshFile();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class DevToolsIndex extends JSONFileUtils{
        @Override
        public Object put(String key, Object value) {
            Object k = super.put(key, value);
            if(key.equalsIgnoreCase("activeRemote")){
                    if((boolean) value){
                        Client.getInstance().setDevToolsAccess(true);
                    }
            }
            if(key.equalsIgnoreCase("token")){
                if(value instanceof String){
                    String s = (String) value;
                    Client.getInstance().setDevToolsToken(new String(Base64.getDecoder().decode(s)));
                }
            }
            return k;
        }
    }
}
