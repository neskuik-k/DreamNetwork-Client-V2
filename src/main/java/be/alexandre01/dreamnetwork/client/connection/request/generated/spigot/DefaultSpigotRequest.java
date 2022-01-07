package be.alexandre01.dreamnetwork.client.connection.request.generated.spigot;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.players.Player;
import be.alexandre01.dreamnetwork.client.connection.core.players.ServicePlayersManager;
import be.alexandre01.dreamnetwork.client.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.service.JVMService;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultSpigotRequest extends RequestBuilder {
    public DefaultSpigotRequest() {
        Client c = Client.getInstance();
        requestData.put(RequestType.SPIGOT_HANDSHAKE_SUCCESS,(message,client, args) -> {
            message.set("STATUS","SUCCESS");
            message.set("PROCESSNAME", client.getJvmService().getJvmExecutor().getName()+"-"+client.getJvmService().getId());
            return message;
        });
        requestData.put(RequestType.SPIGOT_EXECUTE_COMMAND,(message,client, args) -> {
            message.set("CMD", args[0]);
            return message;
        });
        requestData.put(RequestType.SPIGOT_NEW_SERVERS,(message, client, args) -> {
            message.set("SERVERS", Arrays.asList(args));
            return message;
        });
        requestData.put(RequestType.SPIGOT_REMOVE_SERVERS,(message, client, args) -> {
            message.set("S", Arrays.asList(args));
            return message;
        });
        requestData.put(RequestType.CORE_STOP_SERVER, ((message, client, args) -> {
            return message;
        }));
        requestData.put(RequestType.SPIGOT_UPDATE_PLAYERS,(message, client, args) -> {
            ArrayList<String> s = new ArrayList<>();
            for(Object o : args){
                if(o instanceof Player){
                    Player p = (Player) o;
                    if(p.getServer().getJvmService() == null){
                        continue;
                    }
                    JVMService service = p.getServer().getJvmService();
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.getId()).append(";");
                    String name = service.getJvmExecutor().getName()+"-"+service.getId();

                    sb.append(name);
                    if(!c.getServicePlayersManager().getIsRegistered().containsKey(p) || !c.getServicePlayersManager().getIsRegistered().get(p).contains(client)){
                        sb.append(";");
                        sb.append(p.getName());
                        if(p.getUuid() != null){
                            sb.append(";");
                            sb.append(p.getUuid().toString());
                        }

                        c.getServicePlayersManager().getIsRegistered().put(p,client);
                    }
                    s.add(sb.toString());
                }
            }
            message.set("P", s);

            return message;
        });

        requestData.put(RequestType.SPIGOT_UNREGISTER_PLAYERS,(message, client, args) -> {
            ArrayList<Integer> s = new ArrayList<>();

            for(Object o : args){
                if(o instanceof Player){
                    Player p = (Player) o;
                    Client.getInstance().formatter.prStr.println(p.getId());
                    s.add((p.getId()));
                }
            }
            message.set("P", s);
            return message;
        });

        requestData.put(RequestType.CORE_REGISTER_CHANNEL,(message, client, args) -> {
            message.set("CHANNEL", args[0]);
            message.set("MAP",args[1]);
            return message;
        });
    }
}
