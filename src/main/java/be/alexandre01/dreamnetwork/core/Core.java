package be.alexandre01.dreamnetwork.core;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.addons.Addon;
import be.alexandre01.dreamnetwork.api.addons.DreamExtension;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.events.EventsFactory;
import be.alexandre01.dreamnetwork.api.events.list.CoreInitEvent;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;
import be.alexandre01.dreamnetwork.core.addons.AddonsLoader;
import be.alexandre01.dreamnetwork.core.addons.AddonsManager;
import be.alexandre01.dreamnetwork.core.commands.CommandsManager;
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
    @Getter
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
    public Core(){
        //JVM ARGUMENTS
    }

    public void afterConstructor(){
        String s = System.getProperty("ebug");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodeBase","true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
        if(s != null && s.equalsIgnoreCase("true")){
            System.out.println(Chalk.on("DEBUG MODE ENABLED !").bgGreen());
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
        //  Console.setActualConsole("m:default");

        Console.load("m:spiget");
        spigetConsole = new SpigetConsole(Console.getConsole("m:spiget"));


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


        Console console = Console.getConsole("m:default");
        console.defaultPrint = formatter.getDefaultStream();
        DevToolsToken devToolsToken = new DevToolsToken();
        devToolsToken.init();
        Main.getCommandReader().run(console);

        System.out.println("CoreServer is starting...");
        try {
            Thread thread = new Thread(new CoreServer(14520));
            thread.start();
            console.fPrint("The CoreServer System has been started on the port 14520.",Level.INFO);
        } catch (Exception e) {

            console.fPrint(Chalk.on("ERROR CAUSE>> "+e.getMessage()+" || "+ e.getClass().getSimpleName()).red(),Level.SEVERE);
            for(StackTraceElement s : e.getStackTrace()){
                Core.getInstance().formatter.getDefaultStream().println("----->");
                Core.getInstance().getFileHandler().publish(new LogRecord(Level.SEVERE,"----->"));
                console.fPrint("ERROR ON>> "+Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET(),Level.SEVERE);
            }
            if(Core.getInstance().isDebug()){
                e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
            }else {
                formatter.getDefaultStream().println("Please contact the DN developers about this error.");
                fileHandler.publish(new LogRecord(Level.SEVERE,"Please contact the DN developers about this error."));

            }

        }

        console.fPrint(Colors.WHITE_BACKGROUND+Colors.GREEN+"The Network has been successfully started / Do help to get the commands", Level.INFO);


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
                System.out.println("Addon "+addon.getDreamyName()+" has been loaded.");
                addonsManager.registerAddon(extension);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(addon.getDreamyName() + " is not supported");
            }
        });

        //Send CUSTOM REQUESTS TO SERVERS
        Main.getTemplateLoading().createCustomRequestsFile();

        this.bundleManager = new BundleManager();
        bundleManager.init();

        bundleManager.onReady();

        servicePlayersManager = new ServicePlayersManager();


        //MANAGER
        this.channelManager = new DNChannelManager();
        this.clientManager = new ClientManager(this);

        Main.getCommandReader().init();




        addonsManager.getAddons().values().forEach(DreamExtension::start);
        getEventsFactory().callEvent(new CoreInitEvent(getDnCoreAPI()));

    }

    public ArrayList<CoreResponse> getGlobalResponses(){
        return CoreHandler.getGlobalResponses();
    }
}
