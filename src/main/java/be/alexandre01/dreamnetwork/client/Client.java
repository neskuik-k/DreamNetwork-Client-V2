package be.alexandre01.dreamnetwork.client;


import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenManager;
import be.alexandre01.dreamnetwork.client.config.remote.DevToolsToken;
import be.alexandre01.dreamnetwork.client.connection.core.CoreServer;
import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.core.players.ServicePlayersManager;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.client.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.bundle.BService;
import be.alexandre01.dreamnetwork.client.service.bundle.BundleIndex;
import be.alexandre01.dreamnetwork.client.service.bundle.BundleManager;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaIndex;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaReader;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.ASCIIART;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;



import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Client {
    @Getter
    boolean debug = false;
    private InputStream in;
    public Formatter formatter;
    @Getter private FileHandler fileHandler;
    @Getter
    @Setter
    public static Logger logger = Logger.getLogger(Client.class.getName());
    @Getter
    private final static Client instance;
    @Getter
    private JVMContainer jvmContainer;
    @Getter
    private SpigetConsole spigetConsole;
    private StatsConsole statsConsole;
    @Getter @Setter
    private static String username;
    @Getter @Setter
    private CoreHandler coreHandler;
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

    @Getter private DNClientAPI dnClientAPI;

    @Getter private ServicePlayersManager servicePlayersManager;
    static {
        instance = new Client();
    }
    public Client(){
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
        try {
            Thread thread = new Thread(new CoreServer(14520));
            thread.start();
            console.fPrint("The CoreServer System has been started on the port 14520.",Level.INFO);
        } catch (Exception e) {

            console.fPrint(Chalk.on("ERROR CAUSE>> "+e.getMessage()+" || "+ e.getClass().getSimpleName()).red(),Level.SEVERE);
            for(StackTraceElement s : e.getStackTrace()){
                Client.getInstance().formatter.getDefaultStream().println("----->");
                Client.getInstance().getFileHandler().publish(new LogRecord(Level.SEVERE,"----->"));
                console.fPrint("ERROR ON>> "+Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET(),Level.SEVERE);
            }
            if(Client.getInstance().isDebug()){
                e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
            }else {
                formatter.getDefaultStream().println("Please contact the DN developers about this error.");
                fileHandler.publish(new LogRecord(Level.SEVERE,"Please contact the DN developers about this error."));

            }

        }

        console.fPrint(Colors.WHITE_BACKGROUND+Colors.GREEN+"The Network has been successfully started / Do help to get the commands", Level.INFO);


        IScreenManager.load();

        JavaReader javaReader = new JavaReader();
        javaIndex = javaReader.getJavaIndex();


        this.bundleManager = new BundleManager();
        bundleManager.init();

        bundleManager.onReady();

        servicePlayersManager = new ServicePlayersManager();


        //MANAGER
        this.channelManager = new DNChannelManager();
        this.clientManager = new ClientManager(this);

        this.dnClientAPI = new DNClientAPI();
    }
}
