package be.alexandre01.dreamnetwork.core;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.addons.Addon;
import be.alexandre01.dreamnetwork.api.addons.DreamExtension;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.events.list.CoreInitEvent;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;
import be.alexandre01.dreamnetwork.core.accessibility.create.CreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.accessibility.intro.IntroductionConsole;
import be.alexandre01.dreamnetwork.core.addons.AddonsLoader;
import be.alexandre01.dreamnetwork.core.addons.AddonsManager;
import be.alexandre01.dreamnetwork.core.config.remote.DevToolsToken;
import be.alexandre01.dreamnetwork.core.connection.core.CoreServer;
import be.alexandre01.dreamnetwork.core.connection.core.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.core.players.ServicePlayersManager;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.core.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaIndex;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaReader;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
    private SpigetConsole spigetConsole;
    private StatsConsole statsConsole;
    @Getter @Setter
    private static String username;

    @Getter
    private ClientManager clientManager;
    @Getter
    private JavaIndex javaIndex;
    @Getter
    private IDNChannelManager channelManager;
    @Getter
    private BundleManager bundleManager;
    @Getter @Setter private boolean devToolsAccess = false;
    @Getter @Setter private String devToolsToken = null;

    @Getter private AddonsLoader addonsLoader;
    @Getter private AddonsManager addonsManager;

    @Getter private DNCoreAPI dnCoreAPI;

    @Getter private EventsFactory eventsFactory;
    @Getter private ServicePlayersManager servicePlayersManager;
    static {
        instance = new Core();
    }

    @Getter private IntroductionConsole introConsole;
    @Getter private CreateTemplateConsole createTemplateConsole;

    public Core(){
        //JVM ARGUMENTS
    }

    public void afterConstructor(){
        
        String s = System.getProperty("ebug");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodeBase","true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
        if(s != null && s.equalsIgnoreCase("true")){
            System.out.println(LanguageManager.getMessage("core.debugMode"));
            debug = true;
        }


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

        Console.load("m:spiget");
        spigetConsole = new SpigetConsole(Console.getConsole("m:spiget"));
        introConsole = new IntroductionConsole("begin");


        Console.load("m:stats");
        statsConsole = new StatsConsole(Console.getConsole("m:stats"));

                /*
        JVM CONTAINER TO STORE JVMExecutors
         */
        this.jvmContainer = new JVMContainer();

    }

    public void init(){
        formatter = new Formatter();
        formatter.format();
        ASCIIART.sendLogo();
        ASCIIART.sendTitle();


        Console console = Console.getConsole(Console.actualConsole);
        console.defaultPrint = formatter.getDefaultStream();
        DevToolsToken devToolsToken = new DevToolsToken();
        devToolsToken.init();
        Main.getCommandReader().run(console);

        System.out.println(LanguageManager.getMessage("core.server.starting"));
        try {
            CoreServer coreServer;
            Thread thread = new Thread(coreServer = new CoreServer(14520));
            thread.start();
            console.fPrint(LanguageManager.getMessage("core.server.started").replaceFirst("%var%", String.valueOf(coreServer.getPort())), Level.INFO);
        } catch (Exception e) {
            Console.bug(e);
        }

        console.fPrint(LanguageManager.getMessage("core.networkStarted"), Level.INFO);


        IScreenManager.load();

        JavaReader javaReader = new JavaReader();
        javaIndex = javaReader.getJavaIndex();


        eventsFactory = new EventsFactory();
        //LOAD ADDONS
        addonsLoader = new AddonsLoader();
        addonsManager = new AddonsManager(this);
        this.dnCoreAPI = new DNCoreAPI();
        addonsLoader.getAddons().forEach(addon -> {
            Class<?> c = addon.getDefaultClass();
            DreamExtension extension = null;
            try {
                extension = (DreamExtension) c.getDeclaredConstructor(Addon.class).newInstance(addon);
                extension.onLoad();
                System.out.println(LanguageManager.getMessage("core.addon.loaded").replaceFirst("%var%", addon.getDreamyName()));
                addonsManager.registerAddon(extension);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(LanguageManager.getMessage("core.addon.notSupported").replaceFirst("%var%", addon.getDreamyName()));
            }
        });

        //Send CUSTOM REQUESTS TO SERVERS
        Main.getBundlesLoading().createCustomRequestsFile();

        this.bundleManager = Main.getBundleManager();
        bundleManager.init();

        bundleManager.onReady();

        servicePlayersManager = new ServicePlayersManager();


        //MANAGER
        this.channelManager = new DNChannelManager();
        this.clientManager = new ClientManager(this);

        Main.getCommandReader().init();

        console.reloadCompletor();

        createTemplateConsole = new CreateTemplateConsole("","","","","","auto");


        addonsManager.getAddons().values().forEach(DreamExtension::start);
        getEventsFactory().callEvent(new CoreInitEvent(getDnCoreAPI()));

        if(Main.getBundlesLoading().isFirstLoad()){
            Console.setActualConsole("m:introbegin",true,false);
        }

        Console.setBlockConsole(false);
    }

    public ArrayList<CoreResponse> getGlobalResponses(){
        return CoreHandler.getGlobalResponses();
    }
}
