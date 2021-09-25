package be.alexandre01.dreamnetwork.client.api;

import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.json.JSONArray;
import org.json.JSONObject;


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

    public void testReq(){
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.get("http://e14d-2a01-cb10-8700-cd00-5022-f6d7-ceeb-2cdb.ngrok.io/")
                    .asString();
            System.out.println(response.getStatus());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
    public boolean hasValidLicense(String uuid, String secret) {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://api.dreamnetwork.cloud/licenses/validate")
                    .header("Content-Type", "application/x-www-form-urlencoded")
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
                System.out.println(Colors.RED+ "The API is DOWN -> please retry later.");
                System.exit(1);
                return false;
            }
            if(response.getBody().isEmpty()){
                System.out.println(Colors.RED+ "The BODY response is EMPTY -> please retry.");
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

                System.out.println("There is an error with your license: "+sb.toString());
            }catch (Exception e){
                System.out.println("The API Return an untranscribable body");
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
