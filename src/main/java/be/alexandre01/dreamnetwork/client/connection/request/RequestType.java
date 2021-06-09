package be.alexandre01.dreamnetwork.client.connection.request;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum RequestType {
    /**
     * CORE REQUEST
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



    /**
     * SPIGOT REQUEST
     */

    SPIGOT_AUTH(31),
    SPIGOT_HANDSHAKE(32),
    SPIGOT_HANDSHAKE_SUCCESS(33),
    SPIGOT_GET_SERVERS(34),
    SPIGOT_EXECUTE_COMMAND(35),
    SPIGOT_LOG_MESSAGE(36),
    SPIGOT_WARNING_MESSAGE(37),
    SPIGOT_ERROR_MESSAGE(38),
    /**
     * SPIGOT REQUEST
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
    BUNGEECORD_ERROR_MESSAGE(71);

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
