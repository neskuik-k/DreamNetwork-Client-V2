package be.alexandre01.dreamnetwork.client.connection.core.communication;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.logging.Level;

public class AuthentificationResponse extends CoreResponse{
    final CoreHandler coreHandler;
    final private Client client;
    final private Decoder decoder = Base64.getDecoder();

    public AuthentificationResponse() {
        this.client = Client.getInstance();
        this.coreHandler = client.getCoreHandler();
    }

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx, ClientManager.Client client) throws Exception {
        Console.print("Requete entrente->",Level.FINE);
        Console.print(message,Level.FINE);
        ArrayList<ChannelHandlerContext> ctxs = coreHandler.getAllowedCTX();
        if(message.hasRequest()){
            RequestType requestType = message.getRequest();
            Console.print("REQUETE : "+ requestType, Level.FINE);

            if(!coreHandler.getExternalConnection().contains(ctx)){
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


                        if(newClient.getJvmType() == null){
                            Console.print(Colors.RED+"Client "+newClient.getInfo()+" not recognized and tried to connect");
                            return;
                        }
                        if (newClient.getJvmType().equals(JVMContainer.JVMType.PROXY)) {
                            newClient.getRequestManager().sendRequest(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS);
                            Console.print(Colors.YELLOW+"- "+ Colors.CYAN_BOLD+"Proxy "+ newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId()+" lié à DreamNetwork");
                            for(ClientManager.Client devtools : Client.getInstance().getClientManager().getDevTools()){
                                String server = newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId();
                                devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVER, server+";"+newClient.getJvmService().getJvmExecutor().getType()+";"+ newClient.getJvmService().getJvmExecutor().isProxy()+";true");
                            }
                            Main.getInstance().getCoreHandler().getAllowedCTX().add(ctx);
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
                            for(ClientManager.Client devtools : Client.getInstance().getClientManager().getDevTools()){
                                String server = newClient.getJvmService().getJvmExecutor().getName()+"-"+newClient.getJvmService().getId();
                                if(devtools != null)
                                    devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVER, server+";"+newClient.getJvmService().getJvmExecutor().getType()+";"+ newClient.getJvmService().getJvmExecutor().isProxy()+";true");
                            }

                            for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    if(service.getClient() != null){
                                        servers.add(jvmExecutor.getName()+"-"+service.getId());
                                    }
                                }
                            }

                            newClient.getJvmService().getClient().getRequestManager().sendRequest(RequestType.SPIGOT_NEW_SERVERS, servers.toArray(new String[0]));
                            Main.getInstance().getCoreHandler().getAllowedCTX().add(ctx);

                        }
                        break;
                    case DEV_TOOLS_HANDSHAKE:
                        devToolsCheck(requestType, message, ctx, ctxs);
                        break;
                }
            }else {
                switch (requestType) {
                    case DEV_TOOLS_HANDSHAKE:
                        devToolsCheck(requestType, message, ctx, ctxs);
                        break;
                }
            }
        }else {
            if(!ctxs.contains(ctx)){
                ctx.channel().close();
            }
        }
    }

    public void devToolsCheck(RequestType requestType,Message message,ChannelHandlerContext ctx,ArrayList<ChannelHandlerContext> ctxs){

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
                ClientManager.Client devClient = Client.getInstance().getClientManager().registerClient(ClientManager.Client.builder()
                        .coreHandler(Client.getInstance().getCoreHandler())
                        .info(devInfo)
                        .port(devPort)
                        .jvmType(null)
                        .ctx(ctx)
                        .isDevTool(true)
                        .build());

                ArrayList<String> devServers = new ArrayList<>();
                for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(JVMService service : jvmExecutor.getServices()){
                        devServers.add(jvmExecutor.getName()+"-"+service.getId()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";"+ (service.getClient() != null));
                    }
                }


                for(JVMExecutor jvmExecutor : Client.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(JVMService service : jvmExecutor.getServices()){
                        devServers.add(jvmExecutor.getName()+"-"+service.getId()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";" + (service.getClient() != null));
                    }
                }
                String str = String.join(",", devServers);
                ctxs.add(ctx);
                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_HANDSHAKE);

                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVER, str);
                Console.print(Colors.YELLOW+"- "+ Colors.GREEN_BOLD+"Console distante DEVTOOL lié à DreamNetwork sous le nom "+ devUser+" via l'ip "+ devClient.getChannelHandlerContext().channel().remoteAddress());
    }
}
