package be.alexandre01.dreamnetwork.client.connection.core.players;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Player {
    private final int id;
    @Setter private ClientManager.Client server;
    private final String name;
    private final UUID uuid;


    public Player(int id, String name, UUID uuid) {
        this.id = id;

        this.name = name;
        this.uuid = uuid;
    }
    public Player(int id, String name) {
        this.id = id;

        this.name = name;
        this.uuid = null;
    }
}
