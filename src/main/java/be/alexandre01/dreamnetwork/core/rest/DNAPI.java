package be.alexandre01.dreamnetwork.core.rest;

import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DNAPI {
    public String url = "https://api.dreamnetwork.cloud/";

    public static void main(String[] args){



        System.out.println(args);
        DNAPI dnapi = new DNAPI();
            dnapi.hasValidLicense("","");

       /* try {
            dnapi.createUser("alexandre.taillet@gmail.com","alexandre","1234");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/
    }

    public boolean hasValidLicense(String uuid, String secret) {
        //Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://api.dreamnetwork.cloud/licenses/validate")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "DreamNetwork-Client/1.2")
                    .field("uuid", uuid)
                    .field("secret", secret)
                    .asString();
        }catch (UnirestException e){

        }

        if(response == null){
            return false;
        }
        if(response.getStatus() == 201 || response.getStatus() == 200){
            return true;
        }else {
            if(response.getStatus() == 521){
                System.out.println(LanguageManager.getMessage("core.api.down"));
                System.exit(1);
                return false;
            }
            if(response.getBody().isEmpty()){
                System.out.println(LanguageManager.getMessage("core.api.down"));
                System.exit(1);
                return false;
            }
            try {
                JSONObject jsonObject = new JSONObject(response.getBody());
                StringBuilder sb = new StringBuilder();
                sb.append(response.getStatusText() +" -> ");

                Object s = jsonObject.get("message");
                if(s instanceof JSONArray){
                    JSONArray jsonArray = (JSONArray) s;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        sb.append(jsonArray.get(i));
                        if(i != jsonArray.length()-1){
                            sb.append(", ");
                        }
                    }
                }
                if(s instanceof String){
                    sb.append(s);
                }

                System.out.println(LanguageManager.getMessage("core.api.licenceError").replaceFirst("%var%", sb.toString()));
            }catch (Exception e){
                System.out.println(LanguageManager.getMessage("core.api.down"));
            }

        }

        return false;
    }

    public void createUser(String email,String username, String pinCode) throws InterruptedException, IOException, ExecutionException, TimeoutException {

        String param = "email="+email+"&username="+username+"&pinCode="+pinCode;
        AsyncHttpClient client = Dsl.asyncHttpClient();

        ListenableFuture<Response> execute = client.preparePost("https://api.dreamnetwork.cloud/api/user/register/" ).setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").setHeader("Content-Length", ""+param.length()).setBody(param).execute();
        // Configure the client.
        Response response = execute.get(10, TimeUnit.SECONDS);

        if (response.getStatusCode() == HttpConstants.ResponseStatusCodes.OK_200) {
            JsonParser jsonParser = new JsonParser();
           JsonObject jsonObject1 = (JsonObject) jsonParser.parse(response.getResponseBody());

            System.out.println(jsonObject1.toString());
        }
        System.out.println(response);
            


        // Send the HTTP request.
        //channel.writeAndFlush(request);



    }
}
