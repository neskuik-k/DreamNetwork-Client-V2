package be.alexandre01.dreamnetwork.client.connection.core.players;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Player {
    private int id;
    @Setter private ClientManager.Client server;
    private String name;
    private UUID uuid;


    public Player(int id, ClientManager.Client server, String name, UUID uuid) {
        this.id = id;
        this.server = server;
        this.name = name;
        this.uuid = uuid;
    }
}
