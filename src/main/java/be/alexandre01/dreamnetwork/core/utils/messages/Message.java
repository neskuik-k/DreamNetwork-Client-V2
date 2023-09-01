package be.alexandre01.dreamnetwork.core.utils.messages;

import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        super.put("DN-"+id,value);
        System.out.println("Value to be set "+ value);
        return this;
    }
    public Message setCustomObject(String id,Object value){
        super.put("DN-"+id,new Gson().toJson(value));
        return this;
    }

    public Object setInRoot(String id, Object value){
        return super.put(id,value);
    }

    public boolean contains(String key){
        return containsKey("DN-"+key);
    }

    public HashMap<String, Object> getObjectData(){
        return this;
    }
    public int getMessageID(){
        return ((Double) super.get("MID")).intValue();
    }
    @Override
    public Object get(Object key) {
        return super.get("DN-"+key);
    }


    public Object getInRoot(Object key){
        return super.get(key);
    }

    public <T> T get(String key,Class<T> tClass){
        System.out.println("Get hihi");
        Object o = super.get("DN-"+key);
        System.out.println("Getted object "+ o.toString());
        if(o == null){
            return null;
        }
        if((o instanceof LinkedHashMap && tClass != LinkedHashMap.class) || (o instanceof LinkedTreeMap && tClass != LinkedTreeMap.class)){
            System.out.println("Wow");
            System.out.println(o);
            System.out.println(new Gson().toJson(o));
            return new Gson().fromJson(new Gson().toJson(o),tClass);
        }
        return (T) super.get("DN-"+key);
    }
    public Message setChannel(String channel){
        super.put("channel",channel);
        return this;
    }

    public String getChannel(){
        return containsKey("channel") ? (String) super.get("channel") : "core";
    }
    public Message setHeader(String header){
        super.put("header",header);
        return this;
    }
    public void setProvider(String provider){
        super.put("provider",provider);
    }
    public void setSender(String provider){
        super.put("sender",provider);
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

    public Message setRequestInfo(RequestInfo requestType){
        super.put("RI",requestType.id());
        return this;
    }

    public RequestInfo getRequest(){
        return (RequestInfo) RequestType.getByID(((Double) super.get("RI")).intValue());
    }

    public int getRequestID(){
        return ((Double) super.get("RI")).intValue();
    }

    public boolean hasRequest(){
        return containsKey("RI");
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
        System.out.println("GetList "+ tClass);
        Type type = new TypeToken<List<T>>(){}.getType();

        System.out.println("Type > "+ type);
        System.out.println("Wow");

        Object o = get(key);

        System.out.println(o.getClass());

        if(o instanceof String){
            return new Gson().fromJson((String) o,type);
        }
        if(o instanceof LinkedTreeMap){
            LinkedTreeMap<?,?> l = (LinkedTreeMap<?,?>) o;
            List<T> list = (List<T>) new ArrayList<>(l.values());
            return list;
        }

        if(o instanceof List){

        }



       // LinkedTreeMap
        //return new Gson().fromJson(new Gson().toJson(get(key)),type);
        //return (List<T>) get(key,type);
       // return new ArrayList<T>((Collection<? extends T>) Arrays.asList(getString(key).split(",")));
        return (List<T>) o;
    }


    public boolean getBoolean(String key){
        return (boolean) get(key);
        //return (boolean) Boolean.parseBoolean(getString(key));
    }

    public boolean hasChannel(){
        return containsKey("channel");
    }

    public static Message createFromJsonString(String json) {
        try{
            return new Message(new Gson().fromJson(json, HASH_MAP_TYPE));
        }catch (Exception e){
            return null;
        }
        //Message builder = new Message(new Gson().fromJson(json, HASH_MAP_TYPE));
    }



    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
       // gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        return gsonBuilder.create().toJson(this,Message.class);
    }



}