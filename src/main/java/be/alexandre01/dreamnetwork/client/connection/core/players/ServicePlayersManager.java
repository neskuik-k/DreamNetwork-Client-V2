package be.alexandre01.dreamnetwork.client.connection.core.players;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServicePlayersManager {
    @Getter final HashMap<be.alexandre01.dreamnetwork.client.connection.core.communication.Client,ServicePlayersObject> objects = new HashMap<>();
    @Getter final HashMap<Integer, Player> playersMap = new HashMap<>();
    @Getter final ArrayList<ServicePlayersObject> wantToBeDirectlyInformed = new ArrayList<>();
    @Getter HashMap<ServicePlayersObject,ScheduledExecutorService> wantToBeInformed = new HashMap<>();

    @Getter Multimap<Player, be.alexandre01.dreamnetwork.client.connection.core.communication.Client> isRegistered = ArrayListMultimap.create();
    Multimap<be.alexandre01.dreamnetwork.client.connection.core.communication.Client, Player> services = ArrayListMultimap.create();
    Multimap<be.alexandre01.dreamnetwork.client.connection.core.communication.Client,Player> toUpdates =ArrayListMultimap.create();
    Multimap<be.alexandre01.dreamnetwork.client.connection.core.communication.Client,Player> toRemove = ArrayListMultimap.create();
    HashMap<be.alexandre01.dreamnetwork.client.connection.core.communication.Client,Integer> count = new HashMap<>();
    int totalCount = 0;
    public void registerPlayer(Player player){
        playersMap.put(player.getId(),player);
        totalCount++;
    }

    public void removeUpdatingClient(be.alexandre01.dreamnetwork.client.connection.core.communication.Client client){
        ServicePlayersObject s = getObject(client);
        wantToBeInformed.remove(s);
        wantToBeDirectlyInformed.remove(s);
        toUpdates.removeAll(client);
        toRemove.removeAll(client);
    }

    public ServicePlayersObject getObject(be.alexandre01.dreamnetwork.client.connection.core.communication.Client client){
        return objects.get(client);
    }
    public void addUpdatingClient(be.alexandre01.dreamnetwork.client.connection.core.communication.Client client, long time, DataType dataType){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        objects.put(client,new ServicePlayersObject(client,dataType));
        toUpdates.putAll(client,playersMap.values());

        if(dataType == DataType.PLAYERS_LIST){
            service.scheduleAtFixedRate(() -> {
                Collection<Player> pToUpdate = toUpdates.get(client);
                Collection<Player> pToRemove = toRemove.get(client);

                if(!pToRemove.isEmpty())
                    client.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS,pToRemove.toArray());
                toRemove.removeAll(client);

                if(!pToUpdate.isEmpty())
                    client.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS,pToUpdate.toArray());
                toUpdates.removeAll(client);
            },0,time, TimeUnit.MILLISECONDS);
        }

        if(dataType == DataType.PLAYERS_COUNT){
            ArrayList<String> a = new ArrayList<>();
            service.scheduleAtFixedRate(() -> {
                    StringBuilder sb = new StringBuilder();
                    for(be.alexandre01.dreamnetwork.client.connection.core.communication.Client c : count.keySet()){
                        sb.append(c.getJvmService().getJvmExecutor().getName()).append("-").append(c.getJvmService().getId());
                        sb.append(";");
                        sb.append(count.get(client));
                        a.add(sb.toString());
                    }
                    client.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS_COUNT,totalCount,a.toArray());
            },0,time, TimeUnit.MILLISECONDS);
        }

    }

    public void udpatePlayerServer(int id,String server){
        Player player = getPlayer(id);
        String[] args = server.split("-");

        JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[0], JVMContainer.JVMType.SERVER);
        int i;
        try {
            i = Integer.parseInt(args[1]);
        }catch (Exception e){
            return;
        }

        if(jvmExecutor != null){
            JVMService jvmService = jvmExecutor.getService(i);
            if(jvmService != null){
                be.alexandre01.dreamnetwork.client.connection.core.communication.Client client = jvmService.getClient();
                be.alexandre01.dreamnetwork.client.connection.core.communication.Client oldClient = player.getServer();
                if(oldClient != null){
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

                if(!wantToBeDirectlyInformed.isEmpty()){
                    for(ServicePlayersObject c : wantToBeDirectlyInformed){
                        c.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS,player);
                    }
                }

                if(!wantToBeInformed.isEmpty()){
                    for(ServicePlayersObject c : wantToBeInformed.keySet()){
                        toUpdates.put(c.getClient(),player);
                    }
                }

            }
        }
    }


    public Player getPlayer(int id){
        return playersMap.get(id);
    }

    public void unregisterPlayer(int id){
        Player player = getPlayer(id);
        be.alexandre01.dreamnetwork.client.connection.core.communication.Client oldClient = player.getServer();
        if(oldClient != null){
            count.put(oldClient,count.get(oldClient)-1);
            services.remove(oldClient,player);
        }
        playersMap.remove(id);
        services.remove(player.getServer(),player);
        for(ServicePlayersObject c : wantToBeDirectlyInformed){
            c.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_UNREGISTER_PLAYERS,player);
        }
        for(ServicePlayersObject c : wantToBeInformed.keySet()){
            toRemove.put(c.getClient(),player);
        }
    }

    public enum DataType{
        PLAYERS_COUNT, PLAYERS_LIST;
    }

}
