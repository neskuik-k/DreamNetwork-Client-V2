package be.alexandre01.dreamnetwork.api.addons;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.request.CustomRequestInfo;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DreamExtension {
    private Core core;

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

    final void load(Core core){
        this.core = core;
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
        Main.getBundlesLoading().sendCustomsFileToServers(inputStream,fileName);
    }

    public void registerPluginToServers(DreamExtension dreamExtension){
        try {
            Main.getBundlesLoading().sendCustomsFileToServers(new FileInputStream(dreamExtension.getAddon().getFile()),dreamExtension.getAddon().getFile().getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPluginToProxies(InputStream inputStream,String fileName){
        Main.getBundlesLoading().sendCustomsFileToProxies(inputStream,fileName);
    }

    public void registerPluginToProxies(DreamExtension dreamExtension){
        try {
            Main.getBundlesLoading().sendCustomsFileToProxies(new FileInputStream(dreamExtension.getAddon().getFile()),dreamExtension.getAddon().getFile().getName());
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
