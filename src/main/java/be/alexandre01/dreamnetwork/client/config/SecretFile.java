package be.alexandre01.dreamnetwork.client.config;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.json.JSONObject;
import sun.security.util.Debug;

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
        SecretFile.createSecretJson("5a665703-6f5b-4d8f-ba4e-772f3130d6b4","allahuakbar2021");
    }

    public void init() throws IOException {
        BufferedReader file = null;
        
        try {
            file = new BufferedReader( new FileReader(System.getProperty("user.dir")+"/data/.dkeys"));
        } catch (FileNotFoundException e) {
            System.out.println(Colors.RED+"Can't find the secret file.");
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(ConsoleReader.terminal)
                    .build();

            Character mask = Config.isWindows() ? (char)'*' : (char) '•';

          //  reader.setPrompt( Colors.YELLOW+"enter the secret-code > "+Colors.RESET);
            PrintWriter out = new PrintWriter(reader.getTerminal().writer());
            String data;
            try {
                while ((data = reader.readLine( Colors.YELLOW+"enter the secret-code > "+Colors.RESET,mask)) != null){
                    if(data.length() > 0){
                        file= createSecretFile(data);
                        break;
                    }
                }
            }catch (UserInterruptException ex){
                Console.debugPrint(Colors.RED+"exiting...");
                Console.debugPrint(Colors.BLACK+Colors.WHITE_BACKGROUND+"Come back when you have the code on the website "+Colors.ANSI_CYAN+"https://dreamnetwork.cloud/"+Colors.BLACK+Colors.WHITE_BACKGROUND+" and See you later! "+Colors.RESET);
                System.exit(0);
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
        File file = new File(System.getProperty("user.dir")+"/data/.dkeys");
        
        if(!file.exists()){
            file.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(encoded);
        bufferedWriter.flush();
        bufferedWriter.close();
        return new BufferedReader( new FileReader(System.getProperty("user.dir")+"/data/.dkeys"));
    }
    public void deleteSecretFile(){
        File file = new File(System.getProperty("user.dir")+"/data/.dkeys");
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
