package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;

import java.util.function.Consumer;

public interface IServicePlayersManager {
    void registerPlayer(Player player);

    void removeUpdatingClient(AServiceClient client);

    ServicePlayersObject getObject(AServiceClient client);

    void addUpdatingClient(AServiceClient client, long time, DataType dataType);

    void udpatePlayerServer(long id, String server);

    Player getPlayer(long id);

    void unregisterPlayer(long id);

    java.util.HashMap<AServiceClient, ServicePlayersObject> getObjects();

    java.util.HashMap<Long, Player> getPlayersMap();

    java.util.ArrayList<ServicePlayersObject> getWantToBeDirectlyInformed();

    java.util.HashMap<ServicePlayersObject, java.util.concurrent.ScheduledExecutorService> getWantToBeInformed();

    com.google.common.collect.Multimap<Player, AServiceClient> getIsRegistered();

    PlayerHistory getMinutedHistory();
    PlayerHistory getHourHistory();

    void addPlayerJoinListener(Consumer<Player> consumer);

    void addPlayerQuitListener(Consumer<Player> consumer);

    void removePlayerJoinListener(Consumer<Player> consumer);

    void removePlayerQuitListener(Consumer<Player> consumer);



    public enum DataType {
        PLAYERS_COUNT, PLAYERS_LIST;
    }
}
