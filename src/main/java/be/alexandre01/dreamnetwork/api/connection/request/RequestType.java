package be.alexandre01.dreamnetwork.api.connection.request;

import lombok.Getter;
import org.bouncycastle.cert.ocsp.Req;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public abstract class RequestType {
    public final static RequestInfo CUSTOM = new RequestInfo(0);
    /**
     * CORE REQUESTS
     */

    public final static RequestInfo CORE_HANDSHAKE = new RequestInfo(1);
    public final static  RequestInfo CORE_HANDSHAKE_SUCCESS = new RequestInfo(2);
    public final static  RequestInfo CORE_LOG_MESSAGE = new RequestInfo(3);
    public final static RequestInfo CORE_WARNING_MESSAGE =  new RequestInfo(4);
    public final static RequestInfo CORE_ERROR_MESSAGE =  new RequestInfo(5);
    public final static RequestInfo CORE_RETRANSMISSION =  new RequestInfo(6);

    public final static RequestInfo CORE_START_SERVER =  new RequestInfo(7);
    public final static RequestInfo CORE_STOP_SERVER =  new RequestInfo(8);
    public final static RequestInfo CORE_REMOVE_SERVER =  new RequestInfo(9);
    public final static RequestInfo CORE_SPIGET_DOWNLOAD =  new RequestInfo(10);
    public final static RequestInfo CORE_CREATE_SERVER =  new RequestInfo(11);
    public final static RequestInfo CORE_INSTALL_SERVER =  new RequestInfo(12);
    public final static RequestInfo CORE_UPDATE_PLAYER = new RequestInfo(13);
    public final static RequestInfo CORE_REMOVE_PLAYER = new RequestInfo(14);
    public final static RequestInfo CORE_REGISTER_CHANNEL = new RequestInfo(15);
    public final static RequestInfo CORE_UNREGISTER_CHANNEL = new RequestInfo(16);
    public final static RequestInfo CORE_ASK_DATA = new RequestInfo(17);
    public final static RequestInfo CORE_STOP_PROXY = new RequestInfo(18);
    /**
     * SPIGOT REQUESTS
     */

    public final static RequestInfo SPIGOT_AUTH = new RequestInfo(31);
    public final static RequestInfo SPIGOT_HANDSHAKE = new RequestInfo(32);
    public final static RequestInfo SPIGOT_HANDSHAKE_SUCCESS = new RequestInfo(33);
    public final static RequestInfo SPIGOT_NEW_SERVERS = new RequestInfo(34);
    public final static RequestInfo SPIGOT_REMOVE_SERVERS = new RequestInfo(35);
    public final static RequestInfo SPIGOT_EXECUTE_COMMAND = new RequestInfo(36);
    public final static RequestInfo SPIGOT_LOG_MESSAGE = new RequestInfo(37);
    public final static RequestInfo SPIGOT_WARNING_MESSAGE = new RequestInfo(38);
    public final static RequestInfo SPIGOT_ERROR_MESSAGE = new RequestInfo(39);
    public final static RequestInfo SPIGOT_UPDATE_PLAYERS = new RequestInfo(40);
    public final static RequestInfo SPIGOT_UNREGISTER_PLAYERS = new RequestInfo(41);
    public final static RequestInfo SPIGOT_UPDATE_PLAYERS_COUNT = new RequestInfo(42);
    public final static RequestInfo SPIGOT_UNREGISTER_PLAYERS_COUNT = new RequestInfo(43);


    /**
     * PROXY REQUESTS
     */

    public final static RequestInfo BUNGEECORD_AUTH = new RequestInfo(61);
    public final static RequestInfo BUNGEECORD_HANDSHAKE = new RequestInfo(62);
    public final static RequestInfo BUNGEECORD_HANDSHAKE_SUCCESS = new RequestInfo(63);
    public final static RequestInfo BUNGEECORD_REGISTER_SERVER = new RequestInfo(64);
    public final static RequestInfo BUNGEECORD_UNREGISTER_SERVER = new RequestInfo(65);
    public final static RequestInfo BUNGEECORD_GET_SERVERS = new RequestInfo(66);
    public final static RequestInfo BUNGEECORD_EXECUTE_COMMAND = new RequestInfo(67);
    public final static RequestInfo BUNGEECORD_LOG_MESSAGE = new RequestInfo(68);
    public final static RequestInfo BUNGEECORD_WARNING_MESSAGE = new RequestInfo(69);
    public final static RequestInfo  BUNGEECORD_ERROR_MESSAGE = new RequestInfo(70);

    /*
    DEV TOOLS REQUESTS (SCREEN VIEWER)
     */
    public final static RequestInfo DEV_TOOLS_HANDSHAKE = new RequestInfo(91);
    public final static RequestInfo DEV_TOOLS_HANDHSAKE_SUCCESS = new RequestInfo(92);
    public final static RequestInfo DEV_TOOLS_VIEW_CONSOLE_MESSAGE = new RequestInfo(93);
    public final static RequestInfo DEV_TOOLS_SEND_COMMAND = new RequestInfo(94);
    public final static RequestInfo DEV_TOOLS_NEW_SERVERS = new RequestInfo(95);
    public final static RequestInfo DEV_TOOLS_REMOVE_SERVERS = new RequestInfo(96);




    @Getter private int id;


    static final Map<Integer, RequestInfo> byId = new HashMap<Integer, RequestInfo>();
    static final List<Integer> allIds = new ArrayList<Integer>();


    static {
        //reflections get all fields with RequestInfo and put in hashmap
        for (Field field : RequestType.class.getFields()){
            if(field.getType() == RequestInfo.class){
                try {
                    addRequestInfo((RequestInfo) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static RequestInfo getByID(int id){
        return byId.get(id);
    }

    public static void addRequestInfo(RequestInfo requestInfo){
        byId.put(requestInfo.id, requestInfo);
        allIds.add(requestInfo.id);
    }


}
