package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePlayersObject {
    private final IClient client;
    private final IServicePlayersManager.DataType dataType;

    public ServicePlayersObject(IClient client, IServicePlayersManager.DataType dataType) {
        this.client = client;
        this.dataType = dataType;
    }
}
