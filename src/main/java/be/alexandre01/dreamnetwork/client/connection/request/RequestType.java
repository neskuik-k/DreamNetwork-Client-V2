package be.alexandre01.dreamnetwork.client.connection.request;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum RequestType {
    CUSTOM(0),
    /**
     * CORE REQUESTS
     */

    CORE_HANDSHAKE(1),
    CORE_HANDSHAKE_SUCCESS(2),
    CORE_LOG_MESSAGE(3),
    CORE_WARNING_MESSAGE(4),
    CORE_ERROR_MESSAGE(5),
    CORE_RETRANSMISSION(6),

    CORE_START_SERVER(7),
    CORE_STOP_SERVER(8),
    CORE_REMOVE_SERVER(9),
    CORE_SPIGET_DOWNLOAD(10),
    CORE_CREATE_SERVER(11),
    CORE_INSTALL_SERVER(12),
    CORE_UPDATE_PLAYER(13),
    CORE_REMOVE_PLAYER(14),
    CORE_REGISTER_CHANNEL(15),
    CORE_UNREGISTER_CHANNEL(16),
    CORE_ASK_DATA(17),
    /**
     * SPIGOT REQUESTS
     */

    SPIGOT_AUTH(31),
    SPIGOT_HANDSHAKE(32),
    SPIGOT_HANDSHAKE_SUCCESS(33),
    SPIGOT_NEW_SERVERS(34),
    SPIGOT_REMOVE_SERVERS(35),
    SPIGOT_EXECUTE_COMMAND(36),
    SPIGOT_LOG_MESSAGE(37),
    SPIGOT_WARNING_MESSAGE(38),
    SPIGOT_ERROR_MESSAGE(39),
    SPIGOT_UPDATE_PLAYERS(40),
    SPIGOT_UNREGISTER_PLAYERS(41),


    /**
     * BUNGEECORD REQUESTS
     */

    BUNGEECORD_AUTH(61),
    BUNGEECORD_HANDSHAKE(62),
    BUNGEECORD_HANDSHAKE_SUCCESS(63),
    BUNGEECORD_REGISTER_SERVER(65),
    BUNGEECORD_UNREGISTER_SERVER(66),
    BUNGEECORD_GET_SERVERS(67),
    BUNGEECORD_EXECUTE_COMMAND(68),
    BUNGEECORD_LOG_MESSAGE(69),
    BUNGEECORD_WARNING_MESSAGE(70),
    BUNGEECORD_ERROR_MESSAGE(71),

    /*
    DEV TOOLS REQUESTS (SCREEN VIEWER)
     */
    DEV_TOOLS_HANDSHAKE(91),
    DEV_TOOLS_HANDHSAKE_SUCCESS(92),
    DEV_TOOLS_VIEW_CONSOLE_MESSAGE(93),
    DEV_TOOLS_SEND_COMMAND(94),
    DEV_TOOLS_NEW_SERVER(95),
    DEV_TOOLS_REMOVE_SERVER(96);



    @Getter private int id;
    private static final Map<Integer, RequestType> byId = new HashMap<Integer, RequestType>();
    RequestType(Integer id){
        this.id = id;
    }


    static {
        for (RequestType e : RequestType.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("id" + e.getId()+ " is not correct.");
            }
        }
    }

    public static RequestType getByID(int id){
        return byId.get(id);
    }


}
