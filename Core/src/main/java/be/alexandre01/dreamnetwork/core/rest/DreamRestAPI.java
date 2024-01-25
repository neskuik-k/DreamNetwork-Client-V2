package be.alexandre01.dreamnetwork.core.rest;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.websocket.WebSocketServer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 23/11/2023 at 17:01
*/
public class DreamRestAPI {
    HttpClient httpClient;
    WebSocketServer webSocketServer;

    @Getter @Setter
    String token;

   @Getter
   String currentKey;

   @Getter
    UUID uuid;
    public static void main(String[] args) {
        DreamRestAPI dreamRestAPI = new DreamRestAPI();
        dreamRestAPI.webSocketServer = new WebSocketServer(8080, "localhost","eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9");
        dreamRestAPI.create();
        System.out.println(dreamRestAPI.checkup("eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9", "8081"));

    }


    public void create(){
        httpClient = HttpClient.create();

    }
    public String checkup(String token,String port){
        this.token = token;
        String response = httpClient.post().uri("https://devnode.dreamnetwork.cloud:8080/checkup")
         .send((httpClientRequest, outbound) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("token", token);
            jsonNode.put("port", port);
            AtomicReference<String> connectIP = new AtomicReference<>("wss://%current%:2352");
             YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(wsSettings -> {
                 String prefix = wsSettings.isSigned() ? "wss://" : "ws://";

                 if(wsSettings.getMethod().equals(WSSettings.Method.LOCALHOST)){
                     connectIP.set("wss://localhost.direct:"+wsSettings.getPort());
                 }

                 if(wsSettings.getMethod().equals(WSSettings.Method.NONE)){
                     connectIP.set(prefix+"%current%"+":"+wsSettings.getPort());
                 }

                 if(wsSettings.getMethod().equals(WSSettings.Method.CUSTOM) && wsSettings.getForceURL() != null){
                     connectIP.set(prefix+wsSettings.getForceURL()+":"+wsSettings.getPort());
                 }

                 if(wsSettings.getMethod().equals(WSSettings.Method.TUNNEL) && wsSettings.getForceURL() != null){
                     connectIP.set("wss://tunnel.dreamnetwork.cloud/websocket?target_ip=%current%");
                 }
             });
            jsonNode.put("connectIP", connectIP.get());
            // json
             httpClientRequest.addHeader("Content-Type", "application/json");
                httpClientRequest.addHeader("Accept", "application/json");
                httpClientRequest.addHeader("User-Agent", "DreamNetwork");
            return outbound.sendString(Mono.just(jsonNode.toString()));
        }).responseSingle((httpClientResponse, byteBufMono) -> {
                    System.out.println("Response status code: " + httpClientResponse.status().code());
                    System.out.println("Response content: " + httpClientResponse.fullPath());
                    return byteBufMono.asString();
        }).block();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        System.out.println("Response => "+response);
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            System.out.println(jsonNode.get("message").asText());
            if(jsonNode.get("data") == null) return null;
            JsonNode data = jsonNode.get("data");
            System.out.println(data.asText());
            //if(data.isEmpty()) return null;
            String[] split = data.asText().split(";");

            currentKey = split[0];
            uuid = UUID.fromString(split[1]);
            return currentKey;
        }catch (Exception e){
            System.out.println("Error while parsing json");
            e.printStackTrace();
            return null;
        }
    }
}
