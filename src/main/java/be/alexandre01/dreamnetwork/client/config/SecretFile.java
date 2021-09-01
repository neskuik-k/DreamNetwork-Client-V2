package be.alexandre01.dreamnetwork.client.config;

import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Data
public class SecretFile {
    private String secret;
    private String uuid;
    private Console console;

    public static void main(String[] args){
      //  SecretFile.createSecretJson("5a665703-6f5b-4d8f-ba4e-772f3130d6b4","EnertuxAimeLesStickers");
    }

    public void init() throws IOException {
        BufferedReader file = null;

        try {
            file = new BufferedReader( new FileReader(System.getProperty("user.dir")+"/.dkeys"));
        } catch (FileNotFoundException e) {
            System.out.println(Colors.RED+"Can't find the secret file.");
            jline.console.ConsoleReader reader =  ConsoleReader.sReader;

            Character mask = Config.isWindows() ? (char)'*' : (char) '•';

            reader.setPrompt( Colors.YELLOW+"enter the secret-code > "+Colors.RESET);
            PrintWriter out = new PrintWriter(reader.getOutput());
            String data;
            while ((data = reader.readLine(mask)) != null){
                if(data.length() > 0){
                    file= createSecretFile(data);
                    break;
                }
            }
        }
        String line;

        while ((line = file.readLine()) != null) {
            try {
                String decoded = new String(Base64.getDecoder().decode(line));
                JSONObject json = new JSONObject(decoded);
                secret = json.getString("secret");
                uuid = json.getString("uuid");
                break;
            }catch (Exception e){
                System.out.println("Can't decode the secret code.");
                break;
            }
        }

        file.close();
    }

    public BufferedReader createSecretFile(String encoded) throws IOException {
        File file = new File(System.getProperty("user.dir")+"/.dkeys");

        if(!file.exists()){
            file.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(encoded);
        bufferedWriter.flush();
        bufferedWriter.close();
        return new BufferedReader( new FileReader(System.getProperty("user.dir")+"/.dkeys"));
    }
    public void deleteSecretFile(){
        File file = new File(System.getProperty("user.dir")+"/.dkeys");
        if(file.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }
    }


    public static void createSecretJson(String uuid, String secret){
        JSONObject json = new JSONObject();
        json.put("uuid",uuid).put("secret",secret);

        System.out.println(json.toString());
        System.out.println(Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8)));

    }

}
