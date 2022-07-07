package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.connection.core.players.ServicePlayersManager;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePlayersObject {
    private final IClient client;
    private final ServicePlayersManager.DataType dataType;

    public ServicePlayersObject(IClient client, ServicePlayersManager.DataType dataType) {
        this.client = client;
        this.dataType = dataType;
    }
}
