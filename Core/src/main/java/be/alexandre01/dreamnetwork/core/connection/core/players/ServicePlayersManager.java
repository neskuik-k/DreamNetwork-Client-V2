package be.alexandre01.dreamnetwork.core.connection.core.players;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.connection.core.players.ServicePlayersObject;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServicePlayersManager implements be.alexandre01.dreamnetwork.api.connection.core.players.IServicePlayersManager {
    @Getter final HashMap<AServiceClient, ServicePlayersObject> objects = new HashMap<>();
    @Getter final HashMap<Integer, Player> playersMap = new HashMap<>();
    @Getter final ArrayList<ServicePlayersObject> wantToBeDirectlyInformed = new ArrayList<>();
    @Getter HashMap<ServicePlayersObject,ScheduledExecutorService> wantToBeInformed = new HashMap<>();

    @Getter Multimap<Player, AServiceClient> isRegistered = ArrayListMultimap.create();
    Multimap<AServiceClient, Player> services = ArrayListMultimap.create();
    Multimap<AServiceClient,Player> toUpdates =ArrayListMultimap.create();
    Multimap<AServiceClient,Player> toRemove = ArrayListMultimap.create();
    HashMap<AServiceClient,Integer> count = new HashMap<>();
    int totalCount = 0;
    @Override
    public void registerPlayer(Player player){
        playersMap.put(player.getId(),player);
        totalCount++;
    }

    @Override
    public void removeUpdatingClient(AServiceClient client){
        ServicePlayersObject s = getObject(client);
        wantToBeInformed.remove(s);
        wantToBeDirectlyInformed.remove(s);
        toUpdates.removeAll(client);
        toRemove.removeAll(client);
    }

    @Override
    public ServicePlayersObject getObject(AServiceClient client){
        return objects.get(client);
    }
    @Override
    public void addUpdatingClient(AServiceClient client, long time, DataType dataType){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        objects.put(client,new ServicePlayersObject(client,dataType));
        toUpdates.putAll(client,playersMap.values());

        if(dataType == DataType.PLAYERS_LIST){
            service.scheduleAtFixedRate(() -> {
                Collection<Player> pToUpdate = toUpdates.get(client);
                Collection<Player> pToRemove = toRemove.get(client);

                if(!pToRemove.isEmpty())
                    client.getRequestManager().sendRequest(RequestType.SERVER_UPDATE_PLAYERS,pToRemove.toArray());
                toRemove.removeAll(client);

                if(!pToUpdate.isEmpty())
                    client.getRequestManager().sendRequest(RequestType.SERVER_UPDATE_PLAYERS,pToUpdate.toArray());
                toUpdates.removeAll(client);
            },0,time, TimeUnit.MILLISECONDS);
        }

        if(dataType == DataType.PLAYERS_COUNT){
            ArrayList<String> a = new ArrayList<>();
            service.scheduleAtFixedRate(() -> {
                    StringBuilder sb = new StringBuilder();
                    for(AServiceClient c : count.keySet()){
                        sb.append(c.getJvmService().getJvmExecutor().getName()).append("-").append(c.getJvmService().getId());
                        sb.append(";");
                        sb.append(count.get(client));
                        a.add(sb.toString());
                    }
                    client.getRequestManager().sendRequest(RequestType.SERVER_UPDATE_PLAYERS_COUNT,totalCount,a.toArray());
            },0,time, TimeUnit.MILLISECONDS);
        }

    }

    @Override
    public void udpatePlayerServer(int id, String server){
        Player player = getPlayer(id);

        System.out.println("Updating player on server "+server);

        System.out.println(Core.getInstance().getServicesIndexing().getService(server));

        Core.getInstance().getServicesIndexing().getService(server).ifPresent(service -> {
                AServiceClient client = service.getClient();
                AServiceClient oldClient = player.getServer();
            System.out.println("Updating player on server "+client.getName());
                if(oldClient != null){
                    System.out.println("from  "+oldClient.getName());
                    count.put(oldClient,count.get(oldClient)-1);
                    services.remove(oldClient,player);
                }


                player.setServer(client);

                if(services.containsKey(client)){
                    if(!services.get(client).contains(player)){
                        if(!count.containsKey(client)){
                            count.put(client,0);
                        }
                        count.put(client,count.get(client)+1);
                    }
                }else {
                    count.put(client,1);
                }
                services.put(client, player);


            System.out.println("Want to be informed "+wantToBeInformed.size());
            System.out.println("Want to be directly informed "+wantToBeDirectlyInformed.size());
                if(!wantToBeDirectlyInformed.isEmpty()){
                    for(ServicePlayersObject c : wantToBeDirectlyInformed){
                        c.getClient().getRequestManager().sendRequest(RequestType.SERVER_UPDATE_PLAYERS,player);
                    }
                }

                if(!wantToBeInformed.isEmpty()){
                    for(ServicePlayersObject c : wantToBeInformed.keySet()){
                        toUpdates.put(c.getClient(),player);
                    }
                }
        });

    }


    @Override
    public Player getPlayer(int id){
        return playersMap.get(id);
    }

    @Override
    public void unregisterPlayer(int id){
        Player player = getPlayer(id);
        if(player == null) return;
        AServiceClient oldClient = player.getServer();
        if(oldClient != null){
            count.put(oldClient,count.get(oldClient)-1);
            services.remove(oldClient,player);
        }
        playersMap.remove(id);
        services.remove(player.getServer(),player);
        for(ServicePlayersObject c : wantToBeDirectlyInformed){
            c.getClient().getRequestManager().sendRequest(RequestType.SERVER_UNREGISTER_PLAYERS,player);
        }
        for(ServicePlayersObject c : wantToBeInformed.keySet()){
            toRemove.put(c.getClient(),player);
        }
    }
}
