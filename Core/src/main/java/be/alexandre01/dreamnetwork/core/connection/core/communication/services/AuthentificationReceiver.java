package be.alexandre01.dreamnetwork.core.connection.core.communication.services;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.handler.PacketRequestConverter;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceLinkedEvent;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ServiceClient;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.connection.core.handler.PlayerReceiver;
import be.alexandre01.dreamnetwork.core.connection.core.requests.devtool.DefaultDevToolRequest;
import be.alexandre01.dreamnetwork.core.connection.core.requests.external.DefaultExternalRequest;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualExecutor;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualService;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.logging.Level;

public class AuthentificationReceiver extends CoreReceiver {
    final CoreHandler coreHandler;
    final private Core core;


    final private Decoder decoder = Base64.getDecoder();

    public AuthentificationReceiver(CoreHandler coreHandler) {
        this.core = Core.getInstance();
        this.coreHandler = coreHandler;
    }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx, UniversalConnection client) {
        Console.printLang("connection.core.communication.enteringRequest", Level.FINE);
        Console.print(message,Level.FINE);

        if(message == null || !message.hasRequest()){
            //System.out.println(message.hasRequest()+" :c");
            if(!coreHandler.getAllowedCTX().contains(ctx)){
                ctx.channel().close();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onReceive(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
            RequestInfo requestInfo = message.getRequest();
            Console.printLang("connection.core.communication.request", Level.FINE, requestInfo.name());

            ArrayList<ChannelHandlerContext> ctxs = coreHandler.getAllowedCTX();
            ClientManager clientManager = Core.getInstance().getClientManager();
            if(!coreHandler.getExternalConnections().contains(ctx)){
               // System.out.println(message);
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
                        ExternalClient extClient = new ExternalClient(0,info,ctx, coreHandler);

                        extClient.setClientManager(clientManager);
                        clientManager.getClientsByConnection().put(ctx,extClient);

                        coreHandler.getAllowedCTX().add(ctx);
                        /*coreHandler.getResponses().add(new BaseResponse());
                        coreHandler.getResponses().add(new ExternalResponse());*/

                        extClient.setName("ExternalClient:"+ ctx.channel().remoteAddress().toString().split(":")[0]);
                        extClient.getRequestManager().getRequestBuilder().addRequestBuilder(new DefaultExternalRequest());
                        extClient.getRequestManager().getRequestBuilder().addRequestBuilder(new DefaultDevToolRequest());
                        extClient.getCoreHandler().getResponses().add(new BaseReceiver());
                        extClient.getCoreHandler().getResponses().add(new PacketRequestConverter());



                        String connectionId;
                        while (clientManager.getExternalTools().containsKey(connectionId = RandomStringUtils.random(6, true, true).toLowerCase())) {
                            // loop until we get a unique id
                        }
                        clientManager.getExternalTools().put(connectionId,extClient);
                        extClient.getRequestManager().sendRequest(RequestType.CORE_HANDSHAKE_STATUS,"SUCCESS",connectionId);
                        return;
                    }
                    int port = message.getInt("PORT");

                    boolean isExternal = false;
                    Console.print("CREATE CLIENT", Level.FINE);
                    if(message.contains("EXTERNAL")){
                        if(message.getBoolean("EXTERNAL"))
                            isExternal = true;
                    }
                    ServiceClient newClient = ServiceClient.builder()
                            .coreHandler(coreHandler)
                            .info(info)
                            .port(port)
                            .jvmType(null)
                            .ctx(ctx)
                            .isExternalService(isExternal)
                            .build();


                        // find service for this external client and link
                        if(message.contains("NAME") && message.contains("ID") && message.contains("PORT")){
                            String fullName = message.getString("NAME");
                            String[] split = fullName.split("/");


                            //get all array 0 to length-2
                            StringBuilder bundleName = new StringBuilder();
                            for(int i = 0; i < split.length-1; i++){
                                bundleName.append(split[i]);
                            }
                            String name = split[split.length-1];
                            String[] splittedName = name.split("-");
                            name = splittedName[0];
                            int id = Integer.parseInt(splittedName[1]);
                            newClient.setName(fullName);

                            int p = message.getInt("PORT");
                            newClient.setPort(p);

                            BundleManager bundleManager = Core.getInstance().getBundleManager();

                            // check bundle name if it has an another name on this client (main => main_1)
                            String idString = message.getString("ID");
                            System.out.println(idString);
                            ExternalClient extClient = clientManager.getExternalTools().get(idString);
                            System.out.println(""+extClient);
                            System.out.println(bundleName.toString());
                            System.out.println(""+bundleManager.getBundlesNamesByTool().containsColumn(bundleName.toString()));
                            System.out.println(""+bundleManager.getBundlesNamesByTool().containsRow(client));
                            System.out.println(""+bundleManager.getBundlesNamesByTool().contains(client,bundleName.toString()));
                            System.out.println(""+bundleManager.getBundlesNamesByTool().rowKeySet());
                            System.out.println(""+bundleManager.getBundlesNamesByTool().columnKeySet());
                            System.out.println(bundleManager.getBundlesNamesByTool().toString());


                            String newBundleName = bundleManager.getBundlesNamesByTool().get(extClient,bundleName.toString());

                            System.out.println(newBundleName);
                            // search VirtualService in VirtualBundle
                            if(bundleManager.getVirtualBundles().containsColumn(bundleName.toString())) {
                                BundleData bundleData = bundleManager.getVirtualBundles().get(extClient,bundleName.toString());
                                VirtualExecutor virtualExecutor = (VirtualExecutor) bundleData.getExecutors().get(name);
                                if (virtualExecutor == null) {
                                    Console.print(Colors.RED + "VirtualExecutor not found");
                                    return;
                                }
                                VirtualService virtualService = (VirtualService) virtualExecutor.createOrGetService(id);
                                virtualService.setId(id);
                                virtualService.setPort(p);
                                virtualService.setClient((AServiceClient) client);
                                newClient.setJvmService(virtualService);
                                virtualService.getExecutorCallbacks().ifPresent(executorCallbacks -> {
                                    if(executorCallbacks.onConnect != null)
                                        executorCallbacks.onConnect.forEach(iCallbackConnect -> iCallbackConnect.whenConnect(virtualService, newClient));
                                });
                            }else {
                                System.out.println("No bundle found");
                                ctx.close();
                                return;
                            }


                        }

                    client = Core.getInstance().getClientManager().registerClient(newClient);
                    if(client == null){
                         return;
                    }
                    coreHandler.getResponses().add(new BaseReceiver());
                    coreHandler.getResponses().add(new PacketRequestConverter());
                    coreHandler.getResponses().add(new PlayerReceiver());
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
                                                jvmService.getFullIndexedName(),
                                                remoteAdress[0].replaceAll("/", ""),
                                                jvmService.getPort(),jvmService.getJvmExecutor().getType().name());
                                    }
                                }
                            }
                        }
                        Console.printLang("connection.core.communication.proxyLinked", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        newClient.getJvmService().getExecutorCallbacks().ifPresent(executorCallbacks -> {
                            if(executorCallbacks.onConnect != null)
                                executorCallbacks.onConnect.forEach(iCallbackConnect -> iCallbackConnect.whenConnect(newClient.getJvmService(), newClient));
                        });

                        if(newClient.getJvmService().getScreen() == null){
                            new Screen(newClient.getJvmService());
                            Console.printLang("commands.service.screen.backupingService", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService());
                        }
                        this.core.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.core.getDnCoreAPI(), newClient, newClient.getJvmService()));

                        for (ExternalClient devtools : Core.getInstance().getClientManager().getExternalTools().values()) {
                            String server = newClient.getJvmService().getFullName();
                            devtools.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, server + ";" + newClient.getJvmService().getJvmExecutor().getType() + ";" + newClient.getJvmService().getJvmExecutor().isProxy() + ";true");
                        }
                       coreHandler.getAllowedCTX().add(ctx);
                    }
                    if (newClient.getJvmType().equals(JVMContainer.JVMType.SERVER)) {
                        newClient.getRequestManager().sendRequest(RequestType.SERVER_HANDSHAKE_SUCCESS);
                        ServiceClient proxy = Core.getInstance().getClientManager().getProxy();
                        String[] remoteAdress = ctx.channel().remoteAddress().toString().split(":");

                        if(proxy != null){
                            proxy.getRequestManager().sendRequest(RequestType.PROXY_REGISTER_SERVER,
                                    newClient.getJvmService().getFullName(),
                                    newClient.getJvmService().getFullIndexedName(),
                                    remoteAdress[0].replaceAll("/", ""),
                                    newClient.getPort(),newClient.getJvmService().getJvmExecutor().getType().name());
                        }

                        Console.printLang("connection.core.communication.serverLinked", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        newClient.getJvmService().getExecutorCallbacks().ifPresent(executorCallbacks -> {
                            if(executorCallbacks.onConnect != null)
                                executorCallbacks.onConnect.forEach(iCallbackConnect -> iCallbackConnect.whenConnect(newClient.getJvmService(), newClient));
                        });
                        if(newClient.getJvmService().getScreen() == null){
                            new Screen(newClient.getJvmService());
                            Console.printLang("commands.service.screen.backupingService", newClient.getJvmService().getJvmExecutor().getFullName(), newClient.getJvmService().getId());
                        }
                        this.core.getEventsFactory().callEvent(new CoreServiceLinkedEvent(this.core.getDnCoreAPI(), newClient, newClient.getJvmService()));

                        List<String> servers = new ArrayList<>();



                        for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().getJVMExecutors()) {
                            String type = jvmExecutor.isProxy() ? "p" : "s";

                            //services démarrés
                            if (!jvmExecutor.getServices().isEmpty()) {
                                for (IService service : jvmExecutor.getServices()) {

                                    if (service.getClient() != null) {
                                        String server = newClient.getJvmService().getFullName() + ";" + newClient.getJvmService().getFullIndexedName()+";"+ newClient.getJvmService().getJvmExecutor().getType().name().charAt(0) + ";t;"+type;
                                        //System.out.println(service.);

                                        // add servers (if not proxy)
                                        if(!jvmExecutor.isProxy()){
                                            service.getClient().getRequestManager().sendRequest(RequestType.SERVER_NEW_SERVERS, server);
                                        }

                                        servers.add(service.getFullName() +";"+ newClient.getJvmService().getFullIndexedName() +";" + jvmExecutor.getType().name().charAt(0) + ";t;"+ type);
                                    }
                                }
                            } else {
                                //services non démarrés
                                if(jvmExecutor.isConfig() && jvmExecutor.getType() != null){
                                    servers.add(jvmExecutor.getFullName() + ";"+ newClient.getJvmService().getFullIndexedName() + ";" + jvmExecutor.getType().name().charAt(0) + ";f;"+type);
                                }
                            }
                        }
                        for (ExternalClient devtools : Core.getInstance().getClientManager().getExternalTools().values()) {
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

                        newClient.getJvmService().getClient().getRequestManager().sendRequest(RequestType.SERVER_NEW_SERVERS, (Object[]) servers.toArray(new String[0]));
                        // adding channels to the new service
                        DNCoreAPI.getInstance().getChannelManager().sendAllChannels(newClient);
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
                ServiceClient devClient = (ServiceClient) Core.getInstance().getClientManager().registerClient(ServiceClient.builder()
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
                        devServers.add(jvmExecutor.getName()+";"+ jvmExecutor.getCustomName().orElse("n") +";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(service.getFullName()+";"+jvmExecutor.getCustomName().orElse("n")+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";"+ (service.getClient() != null));
                    }
                }


                for(IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().getProxiesExecutors()){
                    if(jvmExecutor.getServices().isEmpty())
                        devServers.add(jvmExecutor.getName()+";"+jvmExecutor.getCustomName().orElse("n")+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";false");

                    for(IService service : jvmExecutor.getServices()){
                        devServers.add(service.getFullName()+";"+jvmExecutor.getCustomName().orElse("n")+";"+jvmExecutor.getType()+";"+jvmExecutor.isProxy()+";" + (service.getClient() != null));
                    }
                }
                String str = String.join(",", devServers);
                ctxs.add(ctx);
                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_HANDSHAKE);

                devClient.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVERS, str);
                Console.print(Colors.YELLOW+"- "+ Colors.GREEN_BOLD+"Console distante DEVTOOL lié à DreamNetwork sous le nom "+ devUser+" via l'ip "+ devClient.getChannelHandlerContext().channel().remoteAddress());
    }
}
