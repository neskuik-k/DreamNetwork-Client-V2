package be.alexandre01.dreamnetwork.client.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.util.CharsetUtil;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.asynchttpclient.Dsl.post;

public class DNAPI {
    public String url = "https://api.dreamnetwork.cloud/";


    public static void main(String[] args){



        System.out.println(args);
        DNAPI dnapi = new DNAPI();
        try {
            dnapi.createUser("alexandre.taillet@gmail.com","alexandre","1234");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
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
