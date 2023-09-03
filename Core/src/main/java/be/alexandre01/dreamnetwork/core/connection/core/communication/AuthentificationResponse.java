package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceLinkedEvent;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.connection.requests.generated.external.DefaultExternalRequest;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.logging.Level;

public class AuthentificationResponse extends CoreResponse {
    final CoreHandler coreHandler;
    final private Core core;


    final private Decoder decoder = Base64.getDecoder();

    public AuthentificationResponse(CoreHandler coreHandler) {
        this.core = Core.getInstance();
        this.coreHandler = coreHandler;
    }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx, IClient client) {
        Console.printLang("connection.core.communication.enteringRequest", Level.FINE);
        Console.print(message,Level.FINE);

        if(message == null || !message.hasRequest()){
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
            Console.printLang("connection.core.communication.request", Level.FINE, requestInfo);

            ArrayList<ChannelHandlerContext> ctxs = coreHandler.getAllowedCTX();

            if(!coreHandler.getExternalConnections().contains(ctx)){
                if (RequestType.CORE_HANDSHAKE.equals(requestInfo)) {
                    Console.print("HANDSHAKE", Level.FINE);
                    if (!message.contains("INFO")) {
                        ctx.channel().close();
                        return;
                    }



                    String info = message.getString("INFO");
                    if(info.contains("ExternalDream")){
                        Console.print(Colors.GREEN_UNDERLINED+"ExternalDream CONNECTION DETECTED !", Level.INFO);
                        coreHandler.getExternalConnections().add(ctx);
                        Client extClient = (Client) Core.getInstance().getClientManager().registerClient(Client.builder()
                                .coreHandler(coreHandler)
                                .info(info)
                                .port(0)
                                .jvmType(null)
                                .isExternalTool(true)
                                .ctx(ctx)
                                .build());
                        coreHandler.getAllowedCTX().add(ctx);

                        extClient.setName("ExternalClient:"+ ctx.channel().remoteAddress().toString().split(":")[0]);
                        extClient.getRequestManager().getRequestBuilder().addRequestBuilder(new DefaultExternalRequest());
                        extClient.getCoreHandler().getResponses().add(new BaseResponse());

                        extClient.getRequestManager().sendRequest(RequestType.CORE_HANDSHAKE_STATUS,"SUCCESS");
                        return;
                    }
                    int port = message.getInt("PORT");

                    boolean isExternal = false;
                    Console.print("CREATE CLIENT", Level.FINE);
                    if(message.contains("EXTERNAL")){
                        if(message.getBoolean("EXTERNAL"))
                            isExternal = true;
                    }
                    Client newClient = (Client) Core.getInstance().getClientManager().registerClient(Client.builder()
                            .coreHandler(coreHandler)
                            .info(info)
                            .port(port)
                            .jvmType(null)
                            .ctx(ctx)
                            .isExternalService(isExternal)
                            .build());

                    if(newClient == null){
                        return;
                    }
                    if (newClient.getJvmType() == null) {
                        Console.printLang("connection.core.communication.unrecognizedClient", newClient.getInfo());
                        return;
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.PROXY)) {
                        newClient.getRequestManager().sendRequest(RequestType.PROXY_HANDSHAKE_SUCCESS);
                        for (IJVMExecutor service : Core.getInstance().getJvmContainer().getServersExecutors()) {
                            if (!service.getServices().isEmpty()) {
                                for (IService jvmService : service.getServices()) {
                                    if (jvmService.getClient() != null) {

                                        Console.printLang("connection.core.communication.recoveringClient", jvmService.getJvmExecutor().getName(), jvmService.getId());
                                        String[] remoteAdress = jvmService.getClient().getChannelHandlerContext().channel().remoteAddress().toString().split(":");
                                        newClient.getRequestManager().sendRequest(RequestType.PROXY_REGISTER_SERVER,
                                                jvmService.getFullName(),
                                                remoteAdress[0].replaceAll("/", ""),
                                                jvmService.getPort(),jvmService.getJvmExecutor().getType().name());
                                    }
                                }
                            }
                        }
                        Console.printLang("connection.core.communication.proxyLinked", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        if(newClient.getJvmService().getExecutorCallbacks() != null){
                            if(newClient.getJvmService().getExecutorCallbacks().onConnect != null){
                                newClient.getJvmService().getExecutorCallbacks().onConnect.whenConnect(newClient.getJvmService(), newClient);
                            }
                        }

                        if(newClient.getJvmService().getScreen() == null){
                            new Screen(newClient.getJvmService());
                            Console.printLang("commands.service.screen.backupingService", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService());
                        }
                        this.core.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.core.getDnCoreAPI(), newClient, newClient.getJvmService()));

                        for (IClient devtools : Core.getInstance().getClientManager().getExternalTools()) {
                            String server = newClient.getJvmService().getFullName();
                            devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, server + ";" + newClient.getJvmService().getJvmExecutor().getType() + ";" + newClient.getJvmService().getJvmExecutor().isProxy() + ";true");
                        }
                       coreHandler.getAllowedCTX().add(ctx);
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.SERVER)) {
                        newClient.getRequestManager().sendRequest(RequestType.SERVER_HANDSHAKE_SUCCESS);
                        Client proxy = Core.getInstance().getClientManager().getProxy();
                        String[] remoteAdress = ctx.channel().remoteAddress().toString().split(":");

                        if(proxy != null){
                            proxy.getRequestManager().sendRequest(RequestType.PROXY_REGISTER_SERVER,
                                    newClient.getJvmService().getFullName(),
                                    remoteAdress[0].replaceAll("/", ""),
                                    newClient.getPort(),newClient.getJvmService().getJvmExecutor().getType().name());
                        }

                        Console.printLang("connection.core.communication.serverLinked", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        if(newClient.getJvmService().getExecutorCallbacks() != null){
                             if(newClient.getJvmService().getExecutorCallbacks().onConnect != null){
                                 newClient.getJvmService().getExecutorCallbacks().onConnect.whenConnect(newClient.getJvmService(), newClient);
                            }
                        }
                        if(newClient.getJvmService().getScreen() == null){
                            new Screen(newClient.getJvmService());
                            Console.printLang("commands.service.screen.backupingService", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        }
                        this.core.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.core.getDnCoreAPI(), newClient, newClient.getJvmService()));

                        ArrayList<String> servers = new ArrayList<>();



                        for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().getJVMExecutors()) {
                            String type = jvmExecutor.isProxy() ? "p" : "s";

                            //services démarrés
                            if (!jvmExecutor.getServices().isEmpty()) {
                                for (IService service : jvmExecutor.getServices()) {

                                    if (service.getClient() != null) {
                                        String server = newClient.getJvmService().getFullName() + ";" + newClient.getJvmService().getJvmExecutor().getType().name().charAt(0) + ";t;"+type;
                                        //System.out.println(service.);
                                        if(!jvmExecutor.isProxy()){
                                            service.getClient().getRequestManager().sendRequest(RequestType.SERVER_NEW_SERVERS, server);
                                        }
                                        servers.add(service.getFullName() + ";" + jvmExecutor.getType().name().charAt(0) + ";t;"+ type);
                                    }
                                }
                            } else {
                                //services non démarrés
                                if(jvmExecutor.isConfig() && jvmExecutor.getType() != null){
                                    servers.add(jvmExecutor.getFullName() + ";" + jvmExecutor.getType().name().charAt(0) + ";f;"+type);
                                }
                            }
                        }
                        for (IClient devtools : Core.getInstance().getClientManager().getExternalTools()) {
                            String server = newClient.getJvmService().getFullName();
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

                        newClient.getJvmService().getClient().getRequestManager().sendRequest(RequestType.SERVER_NEW_SERVERS, servers.toArray(new String[0]));
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

                if(!token.equals(this.core.getDevToolsToken())){
                    ctx.channel().close();
                    return;
                }
                String devInfo = message.getString("INFO");
                String devUser = message.getString("USER");
                int devPort = message.getInt("PORT");

                Console.print("CREATE CLIENT", Level.FINE);
                Client devClient = (Client) Core.getInstance().getClientManager().registerClient(Client.builder()
                        .coreHandler(coreHandler)
                        .info(devInfo)
                        .port(devPort)
                        .jvmType(null)
                        .ctx(ctx)
                        .isExternalTool(true)
                        .build());

                ArrayList<String> devServers = new ArrayList<>();
                for(IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().getServersExecutors()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(service.getFullName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";"+ (service.getClient() != null));
                    }
                }


                for(IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().getProxiesExecutors()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(service.getFullName()+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";" + (service.getClient() != null));
                    }
                }
                String str = String.join(",", devServers);
                ctxs.add(ctx);
                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_HANDSHAKE);

                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, str);
                Console.print(Colors.YELLOW+"- "+ Colors.GREEN_BOLD+"Console distante DEVTOOL lié à DreamNetwork sous le nom "+ devUser+" via l'ip "+ devClient.getChannelHandlerContext().channel().remoteAddress());
    }
}
