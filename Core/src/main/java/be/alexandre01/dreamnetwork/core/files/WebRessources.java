package be.alexandre01.dreamnetwork.core.files;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.service.Infos;
import be.alexandre01.dreamnetwork.core.files.versions.ServerVersion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 27/02/2024 at 16:56
*/
public class WebRessources {
    Multimap<String, ServerVersion> versions = ArrayListMultimap.create();
    boolean ready = false;
    WebRessources() {
        try {
            String link = "https://cdn.dreamnetwork.cloud/hypervisor/versions/versions.json";
            readWebRessources(new URL(link)).whenComplete((s, throwable) -> {

                if (throwable != null) {
                    ready = true;
                    throwable.printStackTrace();
                    return;
                }
                try {
                    JsonNode jsonNode = toNode(s);
                    if(jsonNode == null){
                        throw new JSONException("Cannot parse json");
                    }
                    HashMap<String, Stack<ServerVersion.Infos>> map = new HashMap<>();
                    for (JsonNode versions : jsonNode) {
                        System.out.println(jsonNode);
                        Iterator<Map.Entry<String, JsonNode>> fields = versions.fields();
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> providers = fields.next();
                            System.out.println("Provider: "+providers);
                            String provider = providers.getValue().asText();
                            String url = providers.getValue().get("url").asText();
                            String java = providers.getValue().get("java").asText();
                            String type = providers.getValue().get("type").asText();
                            IContainer.JVMType jvmType = type.equals("proxy") ? IContainer.JVMType.PROXY : IContainer.JVMType.SERVER;
                            ServerVersion.Infos infos = new ServerVersion.Infos(provider,url,java,jvmType);
                            if(map.containsKey(providers.getKey())){
                                map.get(providers.getKey()).add(infos);
                            }else{
                                Stack<ServerVersion.Infos> stack = new Stack<>();
                                stack.add(infos);
                                map.put(providers.getKey(),stack);
                            }
                        }
                    }
                    if(map.isEmpty()){
                        ready = true;
                        throw new JSONException("Cannot parse json");
                    }
                    map.forEach((s1, serverVersions) -> {
                        versions.put(s1,new ServerVersion(serverVersions.toArray(new ServerVersion.Infos[0]), s1));
                    });

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                ready = true;
            }).thenRun(() -> {

                System.out.println("Loaded");
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode toNode(String json) throws JsonProcessingException, JsonMappingException {
        return new JsonMapper().readTree(json);
    }

    public CompletableFuture<String> readWebRessources(URL url){
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            try {
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    stringBuilder.append(line);
                }
                completableFuture.complete(stringBuilder.toString());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }).start();
        return completableFuture;
    }

    public WebRessources block(){
        while (!ready){
            try {
             //   System.out.println("Waiting");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public static void main(String[] args) {
        WebRessources web = new WebRessources().block();
        System.out.println(web.versions);
    }
} 
