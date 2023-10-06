package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicePlayersObject {
    private final AServiceClient client;
    private final IServicePlayersManager.DataType dataType;

    public ServicePlayersObject(AServiceClient client, IServicePlayersManager.DataType dataType) {
        this.client = client;
        this.dataType = dataType;
    }
}
