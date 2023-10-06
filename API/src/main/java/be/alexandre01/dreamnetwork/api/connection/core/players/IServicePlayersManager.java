package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;

public interface IServicePlayersManager {
    void registerPlayer(Player player);

    void removeUpdatingClient(AServiceClient client);

    ServicePlayersObject getObject(AServiceClient client);

    void addUpdatingClient(AServiceClient client, long time, DataType dataType);

    void udpatePlayerServer(int id, String server,String bundle);

    Player getPlayer(int id);

    void unregisterPlayer(int id);

    java.util.HashMap<AServiceClient, ServicePlayersObject> getObjects();

    java.util.HashMap<Integer, Player> getPlayersMap();

    java.util.ArrayList<ServicePlayersObject> getWantToBeDirectlyInformed();

    java.util.HashMap<ServicePlayersObject, java.util.concurrent.ScheduledExecutorService> getWantToBeInformed();

    com.google.common.collect.Multimap<Player, AServiceClient> getIsRegistered();

    public enum DataType {
        PLAYERS_COUNT, PLAYERS_LIST;
    }
}
