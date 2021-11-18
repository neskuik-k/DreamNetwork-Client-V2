package be.alexandre01.dreamnetwork.client.utils.messages;

import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

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
        Console.debugPrint(value);

        put("DN-"+id,value);
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
    public int getRequestID(){
        return ((Double) super.get("RID")).intValue();
    }
    @Override
    public Object get(Object key) {
        return super.get("DN-"+key);
    }

    public <T> T get(String key,Class<T> tClass){
        return (T) super.get("DN-"+key);
    }

    public Message setChannel(String channel){
        put("channel",channel);
        return this;
    }

    public String getChannel(){
        return containsKey("channel") ? (String) super.get("channel") : "core";
    }
    public Message setHeader(String header){
        put("header",header);
        return this;
    }
    public void setProvider(String provider){
        put("provider",provider);
    }
    public void setSender(String provider){
        put("sender",provider);
    }
    public String getProvider(){
        return (String) super.get("provider");
    }
    public String getSender(){
        return (String) super.get("sender");
    }
    public boolean hasProvider(){
        return containsKey("provider");
    }
    public String getHeader(){
        return (String) super.get("header");
    }

    public Message setRequestType(RequestType requestType){
        put("requestType",requestType.getId());
        return this;
    }

    public RequestType getRequest(){
        return (RequestType) RequestType.getByID(((Double) super.get("requestType")).intValue());
    }

    public boolean hasRequest(){
        return containsKey("requestType");
    }

    public JsonObject toJsonObject() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }

    public String getString(String key){
        return String.valueOf(super.get("DN-"+key));
    }

    public int getInt(String key){
        return ((Double) get(key)).intValue();
      //  return (int) Integer.parseInt(getString(key));
    }

    public float getFloat(String key){
        return ((Double) get(key)).floatValue();
       //return (float) Float.parseFloat(getString(key));
    }

    public long getLong(String key){
        return (long) get(key);
        //return (long) Long.parseLong(getString(key));
    }

    public List<?> getList(String key) {
        return (List<?>) get(key);
        //return new ArrayList<>(Arrays.asList(getString(key).split(",")));
    }
    public <T> List<T> getList(String key, Class<T> tClass) {
        return (List<T>) get(key);
       // return new ArrayList<T>((Collection<? extends T>) Arrays.asList(getString(key).split(",")));
    }


    public boolean getBoolean(String key){
        return (boolean) get(key);
        //return (boolean) Boolean.parseBoolean(getString(key));
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

    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
       // gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);


        return gsonBuilder.create().toJson(this,Message.class);
    }


}