package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;

public class AuthentificationResponse extends CoreResponse{
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, ClientManager.Client client) throws Exception {
        Console.print("Requete entrente->",Level.FINE);
        Console.print(message,Level.FINE);
        if(message.hasRequest()){
     /*       Message msgTest = new Message();


            byte[] entry = msgTest.toString().getBytes(StandardCharsets.UTF_8);
            final ByteBuf buf = ctx.alloc().buffer(entry.length); // (2)
            buf.writeBytes(entry);

            ctx.writeAndFlush(buf);*/

            RequestType requestType = message.getRequest();
            Console.print("REQUETE : "+ requestType, Level.FINE);

            switch (requestType){
                case CORE_HANDSHAKE:
                    Console.print("HANDSHAKE", Level.FINE);
                    if(!message.contains("INFO") && !message.contains("PORT") && !message.contains("PASSWORD")){
                        ctx.channel().close();
                        return;
                    }

                    String info = message.getString("INFO");
                    int port = message.getInt("PORT");
                    String password = message.getString("PASSWORD");

                    Console.print("CREATE CLIENT", Level.FINE);
                    ClientManager.Client newClient = Client.getInstance().getClientManager().registerClient(ClientManager.Client.builder()
                            .coreHandler(Client.getInstance().getCoreHandler())
                            .info(info)
                            .port(port)
                            .jvmType(null)
                            .ctx(ctx)
                            .build());

                    if (newClient.getJvmType().equals(JVMContainer.JVMType.PROXY)) {
                        newClient.getRequestManager().sendRequest(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS);
                        Console.print(Colors.YELLOW+"- "+ Colors.CYAN_BOLD+"Proxy "+ newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId()+" lié à DreamNetwork");
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.SERVER)) {
                        newClient.getRequestManager().sendRequest(RequestType.SPIGOT_HANDSHAKE_SUCCESS);
                        ClientManager.Client proxy = Client.getInstance().getClientManager().getProxy();
                        String[] remoteAdress = ctx.channel().remoteAddress().toString().split(":");

                        proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_REGISTER_SERVER,
                                newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId(),
                                remoteAdress[0].replaceAll("/",""),
                                String.valueOf(newClient.getPort()));

                        Console.print(Colors.YELLOW+"- "+ Colors.CYAN_BOLD+"Serveur "+ newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId()+" lié à DreamNetwork");
                        ArrayList<String> servers = new ArrayList<>();
                        for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                            for(JVMService service : jvmExecutor.getServices()){
                                if(service.getClient() != null){
                                    String server = newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId();
                                    service.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_NEW_SERVERS, server);
                                    servers.add(jvmExecutor.getName()+"-"+service.getId());
                                }
                            }
                        }
                        for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                            for(JVMService service : jvmExecutor.getServices()){
                                if(service.getClient() != null){
                                    servers.add(jvmExecutor.getName()+"-"+service.getId());
                                }
                            }
                        }

                        newClient.getJvmService().getClient().getRequestManager().sendRequest(RequestType.SPIGOT_NEW_SERVERS, servers.toArray(new String[0]));


                    }

                    break;
            }
        }
    }
}
