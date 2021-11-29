package be.alexandre01.dreamnetwork.client.connection.core.players;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePlayersObject {
    private final ClientManager.Client client;
    private final ServicePlayersManager.DataType dataType;

    public ServicePlayersObject(ClientManager.Client client, ServicePlayersManager.DataType dataType) {
        this.client = client;
        this.dataType = dataType;
    }
}
