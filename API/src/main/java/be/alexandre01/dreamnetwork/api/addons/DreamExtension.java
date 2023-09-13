package be.alexandre01.dreamnetwork.api.addons;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.connection.core.request.CustomRequestInfo;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DreamExtension {


    @Getter
    DNCoreAPI dnCoreAPI;
    @Getter
    Addon addon;


    private ArrayList<RequestType> requestTypes = new ArrayList<>();

    
    public void onLoad(){
        // do nothing
    }

    public DreamExtension(){
        dnCoreAPI = DNCoreAPI.getInstance();
    }

    public DreamExtension(Addon addon){
        dnCoreAPI = DNCoreAPI.getInstance();
        this.addon = addon;
    }

    final void load(){
        onLoad();
    }

    public void registerRequestType(RequestType requestType){
        for (Field field : requestType.getClass().getFields()){
            if(field.getType().getSuperclass().isInstance(CustomRequestInfo.class)) {
                try {
                    CustomRequestInfo requestInfo = (CustomRequestInfo) field.get(null);
                    requestInfo.setCustomName(requestInfo.name() + "#" + getAddon().getDreamyName().replaceAll(" ", "_"));
                    RequestType.addRequestInfo(requestInfo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException e){
                    //ignore
                }
            }
        }
        requestTypes.add(requestType);
    }

    public void registerPluginToServers(InputStream inputStream,String fileName){
        DNUtils.get().getConfigManager().getFileDispatcher().sendCustomsFileToServers(inputStream,fileName);
    }

    public void registerPluginToServers(DreamExtension dreamExtension){
        try {
            DNUtils.get().getConfigManager().getFileDispatcher().sendCustomsFileToServers(new FileInputStream(dreamExtension.getAddon().getFile()),dreamExtension.getAddon().getFile().getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPluginToProxies(InputStream inputStream,String fileName){
        DNUtils.get().getConfigManager().getFileDispatcher().sendCustomsFileToProxies(inputStream,fileName);
    }

    public void registerPluginToProxies(DreamExtension dreamExtension){
        try {
            DNUtils.get().getConfigManager().getFileDispatcher().sendCustomsFileToProxies(new FileInputStream(dreamExtension.getAddon().getFile()),dreamExtension.getAddon().getFile().getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public void start(){
        // do nothing
    }

    public void stop() {
        // do nothing
    }

}
