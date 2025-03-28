package be.alexandre01.dreamnetwork.core.connection.core.requests.spigot;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultSpigotRequest extends RequestBuilder {
    public DefaultSpigotRequest() {
        Core c = Core.getInstance();
        requestData.put(RequestType.SERVER_HANDSHAKE_SUCCESS,(message,client, args) -> {
            message.set("STATUS","SUCCESS");
            message.set("PROCESSNAME", ((AServiceClient) client).getService().getFullName());
            return message;
        });
        requestData.put(RequestType.SERVER_EXECUTE_COMMAND,(message,client, args) -> {
            message.set("CMD", args[0]);
            return message;
        });
        requestData.put(RequestType.SERVER_NEW_SERVERS,(message, client, args) -> {
            message.set("SERVERS", Arrays.asList(args));
            return message;
        });
        requestData.put(RequestType.SERVER_REMOVE_SERVERS,(message, client, args) -> {
            message.set("SERVERS", Arrays.asList(args));
            return message;
        });
        requestData.put(RequestType.CORE_STOP_SERVER, ((message, client, args) -> {
            return message;
        }));
        requestData.put(RequestType.SERVER_UPDATE_PLAYERS,(message, client, args) -> {
            ArrayList<String> s = new ArrayList<>();
            for(Object o : args){
                if(o instanceof Player){
                    Player p = (Player) o;
                    if(p.getServer() == null || p.getServer().getService() == null){
                        continue;
                    }
                    IService service = p.getServer().getService();
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.getId()).append(";");
                    String name = service.getFullName();

                    sb.append(name);
                    if(!c.getServicePlayersManager().getIsRegistered().containsKey(p) || !c.getServicePlayersManager().getIsRegistered().get(p).contains(client)){
                        sb.append(";");
                        sb.append(p.getName());
                        if(p.getUuid() != null){
                            sb.append(";");
                            sb.append(p.getUuid().toString());
                        }

                        c.getServicePlayersManager().getIsRegistered().put(p, (AServiceClient) client);
                    }
                    s.add(sb.toString());
                }
            }
            message.set("P", s);

            return message;
        });


        requestData.put(RequestType.SERVER_UNREGISTER_PLAYERS,(message, client, args) -> {
            ArrayList<Long> s = new ArrayList<>();

            for(Object o : args){
                if(o instanceof Player){
                    Player p = (Player) o;
                  //  Core.getInstance().formatter.prStr.println(p.getId());
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
        requestData.put(RequestType.CORE_REGISTER_CHANNELS_INFOS,(message, client, args) -> {
            message.set("CHANNELS", args[0]);
            return message;
        });
    }
}
