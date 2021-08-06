package be.alexandre01.dreamnetwork.client;


import be.alexandre01.dreamnetwork.client.commands.CommandReader;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.connection.core.CoreServer;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.client.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.libraries.LoadLibraries;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.ASCIIART;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.PropertyConfigurator;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    @Getter
    boolean debug = false;
    private InputStream in;
    public Formatter formatter;
    @Getter
    @Setter
    public static Logger logger = Logger.getLogger(Client.class.getName());
    @Getter
    public static Client instance;
    @Getter
    private JVMContainer jvmContainer;
    @Getter
    private SpigetConsole spigetConsole;
    @Getter @Setter
    private static String username;
    @Getter @Setter
    private CoreHandler coreHandler;
    @Getter
    private ClientManager clientManager;



    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        new LoadLibraries().init();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //UTF8
        Chalk.setColorEnabled(true);

            System.setProperty("file.encoding","UTF-8");
        Logger.getLogger("io.netty").setLevel(Level.OFF);
        Logger.getLogger("io.netty.util.internal.logging.InternalLoggerFactory").setLevel(Level.OFF);
            String pathSlf4J = Client.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "log4j.properties";
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        Logger.getLogger("").addHandler(ch);
        Logger.getLogger("").setLevel(Level.FINE);


        Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null,null);

        if(Config.isWindows()){
            username = System.getProperty("user.name");

        }else {
            try {
                username = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                username = System.getProperty("user.name");
            }
        }




        
        Console.load("m:default").isRunning = true;
        Thread t = new Thread(Console.getConsole("m:default"));
        t.start();

        instance = new Client();

        new TemplateLoading();

    }

    public static void start(){
        instance = new Client();
    }

    public Client(){

        PropertyConfigurator.configure(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        //JVM ARGUMENTS
        String s = System.getProperty("ebug");
        if(s != null && s.equalsIgnoreCase("true")){
            System.out.println(Chalk.on("DEBUG MODE ENABLED !").bgGreen());
            debug = true;

        }

        FileHandler fh = null;
        try {
            fh = new FileHandler("latest.log");
            fh.setLevel(Level.ALL);
            fh.setFormatter(new ConciseFormatter(false));
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }





       Console.defaultConsole = "m:default";
        Console.actualConsole =  "m:default";

      //  Console.setActualConsole("m:default");

       Console.load("m:spiget");
       spigetConsole = new SpigetConsole(Console.getConsole("m:spiget"));
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
        CommandReader commandReader = new CommandReader(console);
        try {
            Thread thread = new Thread(new CoreServer(14520));
            thread.start();
            console.fPrint("The Netty CoreServer System has been started on  the port 14520.",Level.INFO);
        } catch (Exception e) {
            console.fPrint(Chalk.on("ERROR CAUSE>> "+e.getMessage()+" || "+ e.getClass().getSimpleName()).red(),Level.SEVERE);
            for(StackTraceElement s : e.getStackTrace()){
                Client.getInstance().formatter.getDefaultStream().println("----->");
                console.fPrint("ERROR ON>> "+Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET(),Level.SEVERE);
            }
            if(Client.getInstance().isDebug()){
                e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
            }else {
                Client.getInstance().formatter.getDefaultStream().println("Please contact the DN developpers about this error.");
            }

        }
        console.fPrint(Colors.WHITE_BACKGROUND+Colors.GREEN+"Le Network a été démarré avec succès / Faites help pour avoir les commandes", Level.INFO);


        ScreenManager.load();



        //MANAGER
        this.clientManager = new ClientManager(this);



    }





}
