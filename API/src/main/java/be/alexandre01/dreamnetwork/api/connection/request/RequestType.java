package be.alexandre01.dreamnetwork.api.connection.request;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public abstract class RequestType {
    public final static RequestInfo CUSTOM = new RequestInfo(0, "CUSTOM");
    /**
     * CORE REQUESTS
     */
    public final static RequestInfo CORE_HANDSHAKE = new RequestInfo(1, "CORE_HANDSHAKE");
    public final static RequestInfo CORE_HANDSHAKE_STATUS = new RequestInfo(2, "CORE_HANDSHAKE_STATUS");
    public final static RequestInfo CORE_LOG_MESSAGE = new RequestInfo(3,"CORE_LOG_MESSAGE");
    public final static RequestInfo CORE_WARNING_MESSAGE =  new RequestInfo(4,"CORE_WARNING_MESSAGE");
    public final static RequestInfo CORE_ERROR_MESSAGE =  new RequestInfo(5,"CORE_ERROR_MESSAGE");
    public final static RequestInfo CORE_RETRANSMISSION =  new RequestInfo(6,"CORE_RETRANSMISSION");

    public final static RequestInfo CORE_START_SERVER =  new RequestInfo(7,"CORE_START_SERVER");
    public final static RequestInfo CORE_STOP_SERVER =  new RequestInfo(8,"CORE_STOP_SERVER");
    public final static RequestInfo CORE_REMOVE_SERVER =  new RequestInfo(9,"CORE_REMOVE_SERVER");
    public final static RequestInfo CORE_SPIGET_DOWNLOAD =  new RequestInfo(10,"CORE_SPIGET_DOWNLOAD");
    public final static RequestInfo CORE_CREATE_SERVER =  new RequestInfo(11,"CORE_CREATE_SERVER");
    public final static RequestInfo CORE_INSTALL_SERVER =  new RequestInfo(12,"CORE_INSTALL_SERVER");
    public final static RequestInfo CORE_UPDATE_PLAYER = new RequestInfo(13,"CORE_UPDATE_PLAYER");
    public final static RequestInfo CORE_REMOVE_PLAYER = new RequestInfo(14,"CORE_REMOVE_PLAYER");
    public final static RequestInfo CORE_REGISTER_CHANNEL = new RequestInfo(15,"CORE_REGISTER_CHANNEL");
    public final static RequestInfo CORE_UNREGISTER_CHANNEL = new RequestInfo(16,"CORE_UNREGISTER_CHANNEL");
    public final static RequestInfo CORE_ASK_DATA = new RequestInfo(17,"CORE_ASK_DATA");
    public final static RequestInfo CORE_STOP_PROXY = new RequestInfo(18,"CORE_STOP_PROXY");

    public final static RequestInfo CORE_RESTART_SERVER = new RequestInfo(19,"CORE_RESTART_SERVER");
    public final static RequestInfo CORE_REGISTER_EXTERNAL_EXECUTORS = new RequestInfo(20,"CORE_REGISTER_EXTERNAL_EXECUTORS");


    /**
     * SPIGOT REQUESTS
     */

    public final static RequestInfo SERVER_AUTH = new RequestInfo(31,"SERVER_AUTH");
    public final static RequestInfo SERVER_HANDSHAKE = new RequestInfo(32,"SERVER_HANDSHAKE");
    public final static RequestInfo SERVER_HANDSHAKE_SUCCESS = new RequestInfo(33,"SERVER_HANDSHAKE_SUCCESS");
    public final static RequestInfo SERVER_NEW_SERVERS = new RequestInfo(34,"SERVER_NEW_SERVERS");
    public final static RequestInfo SERVER_REMOVE_SERVERS = new RequestInfo(35,"SERVER_REMOVE_SERVERS");
    public final static RequestInfo SERVER_EXECUTE_COMMAND = new RequestInfo(36,"SERVER_EXECUTE_COMMAND");
    public final static RequestInfo SERVER_LOG_MESSAGE = new RequestInfo(37,"SERVER_LOG_MESSAGE");
    public final static RequestInfo SERVER_WARNING_MESSAGE = new RequestInfo(38,"SERVER_WARNING_MESSAGE");
    public final static RequestInfo SERVER_ERROR_MESSAGE = new RequestInfo(39,"SERVER_ERROR_MESSAGE");
    public final static RequestInfo SERVER_UPDATE_PLAYERS = new RequestInfo(40,"SERVER_UPDATE_PLAYERS");
    public final static RequestInfo SERVER_UNREGISTER_PLAYERS = new RequestInfo(41,"SERVER_UNREGISTER_PLAYERS");
    public final static RequestInfo SERVER_UPDATE_PLAYERS_COUNT = new RequestInfo(42,"SERVER_UPDATE_PLAYERS_COUNT");
    public final static RequestInfo SERVER_UNREGISTER_PLAYERS_COUNT = new RequestInfo(43,"SERVER_UNREGISTER_PLAYERS_COUNT");


    /**
     * PROXY REQUESTS
     */

    public final static RequestInfo PROXY_AUTH = new RequestInfo(61,"PROXY_AUTH");
    public final static RequestInfo PROXY_HANDSHAKE = new RequestInfo(62,"PROXY_HANDSHAKE");
    public final static RequestInfo PROXY_HANDSHAKE_SUCCESS = new RequestInfo(63,"PROXY_HANDSHAKE_SUCCESS");
    public final static RequestInfo PROXY_REGISTER_SERVER = new RequestInfo(64,"PROXY_REGISTER_SERVER");
    public final static RequestInfo PROXY_UNREGISTER_SERVER = new RequestInfo(65,"PROXY_UNREGISTER_SERVER");
    public final static RequestInfo PROXY_GET_SERVERS = new RequestInfo(66,"PROXY_GET_SERVERS");
    public final static RequestInfo PROXY_EXECUTE_COMMAND = new RequestInfo(67,"PROXY_EXECUTE_COMMAND");
    public final static RequestInfo PROXY_LOG_MESSAGE = new RequestInfo(68,"PROXY_LOG_MESSAGE");
    public final static RequestInfo PROXY_WARNING_MESSAGE = new RequestInfo(69,"PROXY_WARNING_MESSAGE");
    public final static RequestInfo  PROXY_ERROR_MESSAGE = new RequestInfo(70,"PROXY_ERROR_MESSAGE");

    /*
    DEV TOOLS REQUESTS (SCREEN VIEWER)
     */
    public final static RequestInfo DEV_TOOLS_HANDSHAKE = new RequestInfo(91,"DEV_TOOLS_HANDSHAKE");
    public final static RequestInfo DEV_TOOLS_HANDHSAKE_SUCCESS = new RequestInfo(92,"DEV_TOOLS_HANDHSAKE_SUCCESS");
    public final static RequestInfo DEV_TOOLS_VIEW_CONSOLE_MESSAGE = new RequestInfo(93,"DEV_TOOLS_VIEW_CONSOLE_MESSAGE");
    public final static RequestInfo DEV_TOOLS_SEND_COMMAND = new RequestInfo(94,"DEV_TOOLS_SEND_COMMAND");
    public final static RequestInfo DEV_TOOLS_NEW_SERVERS = new RequestInfo(95,"DEV_TOOLS_NEW_SERVERS");
    public final static RequestInfo DEV_TOOLS_REMOVE_SERVERS = new RequestInfo(96,"DEV_TOOLS_REMOVE_SERVERS");


    @Getter private int id;


    static final Map<Integer, RequestInfo> byId = new HashMap<Integer, RequestInfo>();
    static final List<Integer> allIds = new ArrayList<Integer>();


    static public final ArrayList<CustomRequestInfo> customRequests = new ArrayList<>();



    static {
        //reflections get all fields with RequestInfo and put in hashmap
        for (Field field : RequestType.class.getFields()){
            if(field.getType() == RequestInfo.class){
                try {
                    RequestInfo requestInfo = (RequestInfo) field.get(null);
                    byId.put(requestInfo.id(),requestInfo);
                    allIds.add(requestInfo.id());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static RequestInfo getByID(int id){
        return byId.get(id);
    }

    public static void addRequestInfo(CustomRequestInfo requestInfo){
        byId.put(requestInfo.id(), requestInfo);
        customRequests.add(requestInfo);
        if(!allIds.contains(requestInfo.id())){
            allIds.add(requestInfo.id());
        }
    }


    public static int getFreeID(){
        int id = 0;
        while(allIds.contains(id)){
            id++;
        }
        return id;
    }
}
