package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;

public interface IServicePlayersManager {
    void registerPlayer(Player player);

    void removeUpdatingClient(IClient client);

    ServicePlayersObject getObject(IClient client);

    void addUpdatingClient(IClient client, long time, DataType dataType);

    void udpatePlayerServer(int id, String server,String bundle);

    Player getPlayer(int id);

    void unregisterPlayer(int id);

    java.util.HashMap<IClient, ServicePlayersObject> getObjects();

    java.util.HashMap<Integer, Player> getPlayersMap();

    java.util.ArrayList<ServicePlayersObject> getWantToBeDirectlyInformed();

    java.util.HashMap<ServicePlayersObject, java.util.concurrent.ScheduledExecutorService> getWantToBeInformed();

    com.google.common.collect.Multimap<Player, IClient> getIsRegistered();

    public enum DataType {
        PLAYERS_COUNT, PLAYERS_LIST;
    }
}
