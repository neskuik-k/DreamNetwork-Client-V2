package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager;
import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.connection.core.players.ServicePlayersObject;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.players.ServicePlayersManager;

import java.util.UUID;

import static be.alexandre01.dreamnetwork.api.connection.core.request.RequestType.*;

public class PlayerReceiver extends CoreReceiver {
    Core core;
    public PlayerReceiver(){
        core = Core.getInstance();
        addRequestInterceptor(CORE_UPDATE_PLAYER, (message, ctx, c) -> {

            IServicePlayersManager s = this.core.getServicePlayersManager();
            int id = message.getInt("ID");
            if (!s.getPlayersMap().containsKey(id)) {
                if (message.contains("P")) {
                    Player player;
                    if (message.contains("U")) {
                        player = new Player(id, message.getString("P"), UUID.fromString(message.getString("U")));
                    } else {
                        player = new Player(id, message.getString("P"));
                    }
                    s.registerPlayer(player);
                } else {
                    return;
                }
            }

            if (message.contains("S")) {
                s.udpatePlayerServer(id, message.getString("S"));
            }
        });
        addRequestInterceptor(CORE_REMOVE_PLAYER,(message, ctx, c) -> {
            IServicePlayersManager s;
            int id;
            s = this.core.getServicePlayersManager();
            id = message.getInt("ID");

            s.unregisterPlayer(id);
        });

        addRequestInterceptor(CORE_ASK_DATA,(message, ctx, client) -> {
            if(!(client instanceof AServiceClient)) return;
            AServiceClient c = (AServiceClient) client;

            IServicePlayersManager s;
            s = this.core.getServicePlayersManager();
            String type = message.getString("TYPE");
            String mode = message.getString("MODE");
            if (mode.equals("ALWAYS")) {
                boolean bo = s.getWantToBeInformed().containsKey(c);

                s.removeUpdatingClient(c);
                if (!bo) {
                    if (type.equalsIgnoreCase("PLAYERS")) {
                        c.getRequestManager().sendRequest(SERVER_UPDATE_PLAYERS, s.getPlayersMap().values().toArray());

                        s.getObjects().put(c, new ServicePlayersObject(c, ServicePlayersManager.DataType.PLAYERS_LIST));
                        s.getWantToBeDirectlyInformed().add(s.getObject(c));
                        return;
                    }
                    if (type.equalsIgnoreCase("PCOUNT")) {
                        s.getObjects().put(c, new ServicePlayersObject(c, ServicePlayersManager.DataType.PLAYERS_COUNT));
                        c.getRequestManager().sendRequest(SERVER_UPDATE_PLAYERS_COUNT, s.getPlayersMap().values().toArray());
                        s.getWantToBeDirectlyInformed().add(s.getObject(c));
                    }
                }
                return;
            }
            if (!message.contains("TIME")) {
                return;
            }
            if (type.equalsIgnoreCase("PLAYERS")) {
                long time = message.getLong("TIME");
                s.removeUpdatingClient(c);
                s.addUpdatingClient(c, time, ServicePlayersManager.DataType.PLAYERS_LIST);
                return;
            }
            if (type.equalsIgnoreCase("PCOUNT")) {
                long time = message.getLong("TIME");
                s.removeUpdatingClient(c);
                s.addUpdatingClient(c, time, ServicePlayersManager.DataType.PLAYERS_COUNT);
            }
        });
    }
}
