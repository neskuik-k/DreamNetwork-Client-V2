package be.alexandre01.dreamnetwork.client.utils.messages;

import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Message extends LinkedHashMap<String, Object> {
    private static final Type HASH_MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    public Message(Map<String, Object> map) {
        super(map);
    }
    public Message() {
        super(new LinkedHashMap<>());
    }
    public Message set(String id,Object value){
        put("DN-"+id,value.toString());
        return this;
    }

    public Message setInRoot(String id,Object value){
        return null;
    }
    public boolean contains(String key){
        return containsKey("DN-"+key);
    }

    public HashMap<String, Object> getObjectData(){
        return this;
    }

    public <T> T getObject(String key,Class<T> tClass){
        return (T) get("DN-"+key);
    }

    public Message setChannel(String channel){
        put("channel",channel);
        return this;
    }

    public String getChannel(){
        return (String) get("channel");
    }
    public Message setHeader(String header){
        put("header",header);
        return this;
    }
    public void setProvider(String provider){
        put("provider",provider);
    }

    public String getProvider(){
        return (String) get("provider");
    }
    public boolean hasProvider(){
        return containsKey("provider");
    }
    public String getHeader(){
        return (String) get("header");
    }

    public Message setRequestType(RequestType requestType){
        put("requestType",String.valueOf(requestType.getId()));
        return this;
    }

    public RequestType getRequest(){
        return (RequestType) RequestType.getByID((int)Integer.parseInt((String) get("requestType")));
    }

    public boolean hasRequest(){
        return containsKey("requestType");
    }

    public JsonObject toJsonObject() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }

    public String getString(String key){
        return String.valueOf(get("DN-"+key));
    }

    public int getInt(String key){
        return (int) Integer.parseInt(getString(key));
    }

    public float getFloat(String key){
        return (float) Float.parseFloat(getString(key));
    }

    public long getLong(String key){
        return (long) Long.parseLong(getString(key));
    }

    public boolean getBoolean(String key){
        return (boolean) Boolean.parseBoolean(getString(key));
    }

    public boolean hasChannel(){
        return containsKey("channel");
    }

    public static Message createFromJsonString(String json) {
        Message builder = new Message(new Gson().fromJson(json, HASH_MAP_TYPE));
        return builder;
    }

    public static boolean isJSONValid(String json) {
        try {
            new Gson().fromJson(json, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);

        String json = new Gson().toJson(this,Message.class);
        return json;
    }
}

