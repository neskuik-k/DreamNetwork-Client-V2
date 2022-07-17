package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceLinkedEvent;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.logging.Level;

public class AuthentificationResponse extends CoreResponse {
    final CoreHandler coreHandler;
    final private Client client;


    final private Decoder decoder = Base64.getDecoder();

    public AuthentificationResponse(CoreHandler coreHandler) {
        this.client = Client.getInstance();
        this.coreHandler = coreHandler;
    }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx, IClient client) {
        Console.print("Requete entrente->",Level.FINE);
        Console.print(message,Level.FINE);

        if(!message.hasRequest()){
            if(!coreHandler.getAllowedCTX().contains(ctx)){
                ctx.channel().close();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, IClient client) throws Exception {
            RequestInfo requestInfo = message.getRequest();
            Console.print("REQUETE : "+ requestInfo, Level.FINE);

            ArrayList<ChannelHandlerContext> ctxs = coreHandler.getAllowedCTX();

            if(!coreHandler.getExternalConnection().contains(ctx)){
                if (RequestType.CORE_HANDSHAKE.equals(requestInfo)) {
                    Console.print("HANDSHAKE", Level.FINE);
                    if (!message.contains("INFO") && !message.contains("PORT") && !message.contains("PASSWORD")) {
                        ctx.channel().close();
                        return;
                    }

                    String info = message.getString("INFO");
                    int port = message.getInt("PORT");
                    String password = message.getString("PASSWORD");

                    Console.print("CREATE CLIENT", Level.FINE);
                    be.alexandre01.dreamnetwork.client.connection.core.communication.Client newClient = Client.getInstance().getClientManager().registerClient(be.alexandre01.dreamnetwork.client.connection.core.communication.Client.builder()
                            .coreHandler(coreHandler)
                            .info(info)
                            .port(port)
                            .jvmType(null)
                            .ctx(ctx)
                            .build());


                    if (newClient.getJvmType() == null) {
                        Console.print(Colors.RED + "Client " + newClient.getInfo() + " not recognized and tried to connect");
                        return;
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.PROXY)) {
                        newClient.getRequestManager().sendRequest(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS);
                        for (IJVMExecutor service : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()) {
                            if (!service.getServices().isEmpty()) {
                                for (IService jvmService : service.getServices()) {
                                    if (jvmService.getClient() != null) {
                                        System.out.println(Colors.RED + "<!>" + Colors.YELLOW + " Recovering " + Colors.PURPLE + jvmService.getJvmExecutor().getName() + "-" + jvmService.getId() + Colors.YELLOW + " on the proxy");
                                        String[] remoteAdress = jvmService.getClient().getChannelHandlerContext().channel().remoteAddress().toString().split(":");
                                        newClient.getRequestManager().sendRequest(RequestType.BUNGEECORD_REGISTER_SERVER,
                                                jvmService.getJvmExecutor().getName() + "-" + jvmService.getId(),
                                                remoteAdress[0].replaceAll("/", ""),
                                                jvmService.getPort(),jvmService.getJvmExecutor().getType().name());
                                    }
                                }
                            }
                        }
                        Console.print(Colors.YELLOW + "- " + Colors.CYAN_BOLD + "Proxy " + newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId() + " lié à DreamNetwork");
                        this.client.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.client.getDnClientAPI(), newClient, newClient.getJvmService()));

                        for (IClient devtools : Client.getInstance().getClientManager().getDevTools()) {
                            String server = newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId();
                            devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, server + ";" + newClient.getJvmService().getJvmExecutor().getType() + ";" + newClient.getJvmService().getJvmExecutor().isProxy() + ";true");
                        }
                       coreHandler.getAllowedCTX().add(ctx);
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.SERVER)) {
                        newClient.getRequestManager().sendRequest(RequestType.SPIGOT_HANDSHAKE_SUCCESS);
                        be.alexandre01.dreamnetwork.client.connection.core.communication.Client proxy = Client.getInstance().getClientManager().getProxy();
                        String[] remoteAdress = ctx.channel().remoteAddress().toString().split(":");

                        proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_REGISTER_SERVER,
                                newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId(),
                                remoteAdress[0].replaceAll("/", ""),
                                newClient.getPort(),newClient.getJvmService().getJvmExecutor().getType().name());

                        Console.print(Colors.YELLOW + "- " + Colors.CYAN_BOLD + "Serveur " + newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId() + " lié à DreamNetwork");
                        this.client.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.client.getDnClientAPI(), newClient, newClient.getJvmService()));

                        ArrayList<String> servers = new ArrayList<>();


                        for (IJVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()) {
                            if (!jvmExecutor.getServices().isEmpty()) {
                                for (IService service : jvmExecutor.getServices()) {

                                    if (service.getClient() != null) {
                                        String server = newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId() + ";" + newClient.getJvmService().getJvmExecutor().getType().name().charAt(0) + ";t";
                                        service.getClient().getRequestManager().sendRequest(RequestType.SPIGOT_NEW_SERVERS, server);
                                        servers.add(jvmExecutor.getName() + "-" + service.getId() + ";" + jvmExecutor.getType().name().charAt(0) + ";t");
                                    }
                                }
                            } else {
                                servers.add(jvmExecutor.getName() + ";" + jvmExecutor.getType().name().charAt(0) + ";f");
                            }
                        }
                        for (IClient devtools : Client.getInstance().getClientManager().getDevTools()) {
                            String server = newClient.getJvmService().getJvmExecutor().getName() + "-" + newClient.getJvmService().getId();
                            if (devtools != null)
                                devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, server + ";" + newClient.getJvmService().getJvmExecutor().getType() + ";" + newClient.getJvmService().getJvmExecutor().isProxy() + ";true");
                        }

                          /*  for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    if(service.getClient() != null){
                                        servers.add(jvmExecutor.getName()+"-"+service.getId());
                                    }
                                }
                            }*/

                        newClient.getJvmService().getClient().getRequestManager().sendRequest(RequestType.SPIGOT_NEW_SERVERS, servers.toArray(new String[0]));
                       coreHandler.getAllowedCTX().add(ctx);
                    }
                    return;
                }

                if (RequestType.DEV_TOOLS_HANDSHAKE.equals(requestInfo)) {
                    devToolsCheck(requestInfo, message, ctx, ctxs);
                }
            }else {
                if (RequestType.DEV_TOOLS_HANDSHAKE.equals(requestInfo)) {
                    devToolsCheck(requestInfo, message, ctx, ctxs);
                }
            }
    }

    public void devToolsCheck(RequestInfo requestInfo, Message message, ChannelHandlerContext ctx, ArrayList<ChannelHandlerContext> ctxs){

                Console.print("HANDSHAKE", Level.FINE);
                if(!message.contains("INFO") && !message.contains("PORT") && !message.contains("TOKEN") && !message.contains("USER")){
                    ctx.channel().close();
                    return;
                }
                byte[] byteToken;
                try {
                    byteToken = decoder.decode(message.getString("TOKEN"));
                } catch(IllegalArgumentException iae) {
                    ctx.channel().close();
                    return;
                }
                String token = new String(byteToken);

                if(!token.equals(this.client.getDevToolsToken())){
                    ctx.channel().close();
                    return;
                }
                String devInfo = message.getString("INFO");
                String devUser = message.getString("USER");
                int devPort = message.getInt("PORT");

                Console.print("CREATE CLIENT", Level.FINE);
                be.alexandre01.dreamnetwork.client.connection.core.communication.Client devClient = Client.getInstance().getClientManager().registerClient(be.alexandre01.dreamnetwork.client.connection.core.communication.Client.builder()
                        .coreHandler(coreHandler)
                        .info(devInfo)
                        .port(devPort)
                        .jvmType(null)
                        .ctx(ctx)
                        .isDevTool(true)
                        .build());

                ArrayList<String> devServers = new ArrayList<>();
                for(IJVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(jvmExecutor.getName()+"-"+service.getId()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";"+ (service.getClient() != null));
                    }
                }


                for(IJVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(jvmExecutor.getName()+"-"+service.getId()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";" + (service.getClient() != null));
                    }
                }
                String str = String.join(",", devServers);
                ctxs.add(ctx);
                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_HANDSHAKE);

                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, str);
                Console.print(Colors.YELLOW+"- "+ Colors.GREEN_BOLD+"Console distante DEVTOOL lié à DreamNetwork sous le nom "+ devUser+" via l'ip "+ devClient.getChannelHandlerContext().channel().remoteAddress());
    }
}
