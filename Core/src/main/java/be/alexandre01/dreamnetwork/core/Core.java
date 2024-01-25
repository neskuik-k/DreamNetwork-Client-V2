package be.alexandre01.dreamnetwork.core;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.addons.Addon;
import be.alexandre01.dreamnetwork.api.addons.DreamExtension;
import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.PacketHandlingFactory;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.ConsoleThread;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.events.list.CoreInitEvent;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.api.utils.optional.Facultative;
import be.alexandre01.dreamnetwork.core.connection.core.NettyServer;
import be.alexandre01.dreamnetwork.core.connection.core.ReactorNettyServer;
import be.alexandre01.dreamnetwork.core.connection.core.communication.RateLimiter;
import be.alexandre01.dreamnetwork.core.connection.core.datas.DataLocalObjects;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CallbackManager;
import be.alexandre01.dreamnetwork.core.gui.intro.IntroMenuCore;
import be.alexandre01.dreamnetwork.core.addons.AddonsLoader;
import be.alexandre01.dreamnetwork.core.addons.AddonsManager;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.core.config.remote.DevToolsToken;
import be.alexandre01.dreamnetwork.core.connection.core.CoreServer;
import be.alexandre01.dreamnetwork.core.connection.core.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.core.players.ServicePlayersManager;
import be.alexandre01.dreamnetwork.core.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.core.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.core.installer.InstallerManager;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaIndex;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaReader;
import be.alexandre01.dreamnetwork.core.service.screen.ServicesIndexing;
import be.alexandre01.dreamnetwork.core.service.tasks.GlobalTasks;
import be.alexandre01.dreamnetwork.api.utils.ASCIIART;
import be.alexandre01.dreamnetwork.api.utils.process.ProcessUtils;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.core.websocket.WebSocketServer;
import lombok.Getter;
import lombok.Setter;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Core {
    @Getter @Setter
    boolean debug = false;
    private InputStream in;
    public Formatter formatter;
    @Getter private FileHandler fileHandler;
    @Getter
    @Setter
    public static Logger logger = Logger.getLogger(Core.class.getName());
    @Getter
    private final static Core instance;
    @Getter
    private JVMContainer jvmContainer;
    @Getter
    private InstallerManager installerManager;
    @Getter
    private GlobalTasks globalTasks;

    @Getter
    private ServicesIndexing servicesIndexing;
    private StatsConsole statsConsole;
    @Getter @Setter
    private static String username;

    @Getter @Setter private int port;

    @Getter
    private ClientManager clientManager;
    @Getter
    private JavaIndex javaIndex;
    @Getter
    private IDNChannelManager channelManager;
    @Getter private CallbackManager callbackManager = new CallbackManager();
    @Getter

    private BundleManager bundleManager;
    @Getter @Setter private boolean devToolsAccess = false;
    @Getter @Setter private String devToolsToken = null;

    @Getter private AddonsLoader addonsLoader;
    @Getter private AddonsManager addonsManager;

    @Getter private DNCoreAPI dnCoreAPI;

    @Getter private EventsFactory eventsFactory;
    @Getter private ServicePlayersManager servicePlayersManager;

    @Getter private final PacketHandlingFactory packetHandlingFactory = new PacketHandlingFactory();
    @Getter private final DataLocalObjects dataLocalObjects = new DataLocalObjects();

    @Getter private final RateLimiter rateLimiter = new RateLimiter();

    static {
        instance = new Core();
    }


    public Core(){
        //JVM ARGUMENTS
    }


    public void afterConstructor(){
        String s = System.getProperty("ebug");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodeBase","true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");

        fileHandler = null;
        try {
            fileHandler = new FileHandler("latest.log");

            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new ConciseFormatter(false));
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Console.defaultConsole = "m:default";
        Console.actualConsole =  "m:default";
        Console.getConsole("m:default").isDebug = isDebug();
        Console.setBlockConsole(true);
        //  Console.setActualConsole("m:default");




        Console.load("m:stats");
        statsConsole = new StatsConsole(Console.getConsole("m:stats"));

                /*
        JVM CONTAINER TO STORE JVMExecutors
         */
        if(s != null && s.equalsIgnoreCase("true")){
            //Console.debugPrint("Debug mode enabled");
            System.out.println("Debug mode enabled");
            Core.getInstance().setDebug(true);
        }
        this.jvmContainer = new JVMContainer();
    }

    public void init(){
        formatter = new Formatter();
        formatter.format();
        ASCIIART.sendLogo();
        ASCIIART.sendTitle();


        IntroMenuCore menu = new IntroMenuCore("m:intro");
        menu.buildAndRun();
        Console console = Console.getConsole(Console.actualConsole);
        console.defaultPrint = formatter.getDefaultStream();
        DevToolsToken devToolsToken = new DevToolsToken();
        devToolsToken.init();
        Main.getCommandReader().run(console);

        Console.printLang("core.server.starting");
        try {
            CoreServer coreServer;
            Thread thread;
            if(Main.getGlobalSettings().getConnectionMode().equalsIgnoreCase("reactor-netty")){
                thread = new Thread(coreServer = new ReactorNettyServer(Main.getGlobalSettings().getPort()));
            }else {
                thread = new Thread(coreServer = new NettyServer(Main.getGlobalSettings().getPort()));
            }
            thread.start();


            console.fPrintLang("core.server.started",coreServer.getPort(), Level.INFO);
        } catch (Exception e) {
            Console.bug(e);
        }

        console.fPrintLang("core.networkStarted");


        ScreenManager.load();

        JavaReader javaReader = new JavaReader();
        javaIndex = javaReader.getJavaIndex();

        installerManager = new InstallerManager();

        eventsFactory = new EventsFactory();
        //LOAD ADDONS
        addonsLoader = new AddonsLoader();
        addonsManager = new AddonsManager(this);
        this.dnCoreAPI = new ImplAPI(this);
        addonsLoader.getAddons().forEach(addon -> {
            Class<?> c = addon.getDefaultClass();
            DreamExtension extension = null;
            try {
                extension = (DreamExtension) c.getDeclaredConstructor(Addon.class).newInstance(addon);
                extension.onLoad();
                console.fPrintLang("core.addon.loaded", addon.getDreamyName());
                addonsManager.registerAddon(extension);
            } catch (Exception e) {
                e.printStackTrace();
                console.fPrintLang("core.addon.notSupported", addon.getDreamyName());
            }
        });

        //Send CUSTOM REQUESTS TO SERVERS
        Main.getBundlesLoading().createCustomRequestsFile();

        this.bundleManager = Main.getBundleManager();
        bundleManager.init();

        bundleManager.onReady();

        servicesIndexing = new ServicesIndexing();

        servicePlayersManager = new ServicePlayersManager();


        //MANAGER
        this.channelManager = new DNChannelManager();
        this.clientManager = new ClientManager(this);
        globalTasks = new GlobalTasks();
        globalTasks.loading();

        Main.getCommandReader().init();

        console.reloadCompletors();




        //  getEventsFactory().registerListener(new ServicesTaskListener());

        addonsManager.getAddons().values().forEach(new Consumer<DreamExtension>() {
            @Override
            public void accept(DreamExtension dreamExtension) {
                try {
                    dreamExtension.start();
                }catch (Exception e){
                    Console.bug(e);
                }
            }
        });
        getEventsFactory().callEvent(new CoreInitEvent(getDnCoreAPI()));

        if(Main.getBundlesLoading().isFirstLoad()){
            menu.show();
            // Console.setActualConsole("m:introbegin",true,false);
        }

        if(Main.getGlobalSettings().isCheckDefaultJVMVersion()){
            try {
                Integer ver = ProcessUtils.getDefaultBashJavaVersion(javaIndex.getDefaultJava().getPath());
                if(ver != null){
                    System.out.println("Your default Java "+ Console.getEmoji("coffee","",""," ")+"version is "+ ver);
                    javaIndex.getDefaultJava().setVersion(ver);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(Config.isWindows()){
            Console.printLang("core.windowsWarning");
        }

      /*  CDNFiles cdnFiles = Main.getCdnFiles();
        if(cdnFiles.isInstanced() && cdnFiles.getAddonsToUpdate().size() != 0){
            cdnFiles.getAddonsToUpdate().forEach(name -> {
                Console.printLang("addons.canUpdate", name, name);
            });
        }*/

        boolean debug = Core.getInstance().isDebug();
        if(debug){
            Console.getConsoles().forEach(c -> {
                c.isDebug = true;
            });
        }

        // wait 1 seconds without block the console
        new Thread(() -> {
            try {
                Thread.sleep(500);
                globalTasks.loadTasks();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Console.setBlockConsole(false);
        }).start();



        if(Main.getGlobalSettings().isSIG_IGN_Handler()){
            if(!Config.isWindows()){
                String[] defSIGKILL = {"/bin/sh","-c","stty intr ^C </dev/tty"};
                try {
                    Runtime.getRuntime().exec(defSIGKILL);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(wsSettings -> {
            if(wsSettings.isWsEnabled()){
                WebSocketServer.start(wsSettings.getPort(),Main.getSecretFile().getSecret());
            }
        });
        ConsoleThread.resetAndRun();
    }

    public ArrayList<CoreReceiver> getGlobalResponses(){
        return CoreHandler.getGlobalResponses();
    }
}
