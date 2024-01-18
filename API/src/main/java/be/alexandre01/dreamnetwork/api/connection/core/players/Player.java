package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Player {
    private final long id;
    @Setter private AServiceClient server;
    private final String name;
    private final UUID uuid;
    private final long firstJoin = System.currentTimeMillis();

    public Player(long id, String name, UUID uuid) {
        this.id = id;

        this.name = name;
        this.uuid = uuid;
    }
    public Player(long id, String name) {
        this.id = id;

        this.name = name;
        this.uuid = null;
    }

    public long getTimePlayed(){
        return System.currentTimeMillis() - firstJoin;
    }
}
