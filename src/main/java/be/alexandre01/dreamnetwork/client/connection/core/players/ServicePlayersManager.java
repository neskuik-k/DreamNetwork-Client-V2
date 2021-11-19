package be.alexandre01.dreamnetwork.client.connection.core.players;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
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
    @Getter HashMap<Integer, Player> playersMap = new HashMap<>();
    @Getter ArrayList<ClientManager.Client> wantToBeDirectlyInformed = new ArrayList<>();
    @Getter HashMap<ClientManager.Client,ScheduledExecutorService> wantToBeInformed = new HashMap<>();

    @Getter Multimap<Player,ClientManager.Client> isRegistered = ArrayListMultimap.create();
    Multimap<ClientManager.Client, Player> services = ArrayListMultimap.create();
    Multimap<ClientManager.Client,Player> toUpdates =ArrayListMultimap.create();
    Multimap<ClientManager.Client,Player> toRemove = ArrayListMultimap.create();

    public void registerPlayer(Player player){
        playersMap.put(player.getId(),player);
    }

    public void removeUpdatingClient(ClientManager.Client client){
        wantToBeInformed.remove(client);
        wantToBeDirectlyInformed.remove(client);
        toUpdates.removeAll(client);
        toRemove.removeAll(client);
    }
    public void addUpdatingClient(ClientManager.Client client, long time){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        toUpdates.putAll(client,playersMap.values());
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
                ClientManager.Client client = jvmService.getClient();
                player.setServer(client);
                services.put(client, player);
                for(ClientManager.Client c : wantToBeDirectlyInformed){
                    c.getRequestManager().sendRequest(RequestType.SPIGOT_UPDATE_PLAYERS,player);
                }
                for(ClientManager.Client c : wantToBeInformed.keySet()){
                    toUpdates.put(c,player);
                }
            }
        }
    }


    public Player getPlayer(int id){
        return playersMap.get(id);
    }

    public void unregisterPlayer(int id){
        System.out.println("Remove 3?");
        Player player = getPlayer(id);
        System.out.println("Remove 4?");
        playersMap.remove(id);
        System.out.println("Remove 5?");
        services.remove(player.getServer(),player);
        System.out.println("Remove 6?");
        for(ClientManager.Client c : wantToBeDirectlyInformed){
            System.out.println("Remove 7?");
            System.out.println("Try ?");
            c.getRequestManager().sendRequest(RequestType.SPIGOT_UNREGISTER_PLAYERS,player);
            System.out.println("Remove 8?");
        }
        for(ClientManager.Client c : wantToBeInformed.keySet()){
            System.out.println("Remove 9?");
            toRemove.put(c,player);
            System.out.println("Remove 10?");
        }
    }
}
