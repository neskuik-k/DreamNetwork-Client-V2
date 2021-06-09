package be.alexandre01.dreamnetwork.client;


import be.alexandre01.dreamnetwork.client.commands.CommandReader;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.connection.core.CoreServer;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.client.connection.request.RequestManager;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.client.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.ASCIIART;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
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
    @Getter
    private static String username;
    @Getter @Setter
    private CoreHandler coreHandler;
    @Getter
    private ClientManager clientManager;



    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {


        //UTF8
        Chalk.setColorEnabled(true);

            System.setProperty("file.encoding","UTF-8");

            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null,null);

        if(Config.isWindows()){
            username = System.getProperty("user.name");
            System.out.println(username);
        }else {
            try {
                username = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                username = System.getProperty("user.name");
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if(instance != null){
                        boolean isReady = false;
                        for(JVMExecutor jvmExecutor : instance.getJvmContainer().jvmExecutorsProxy.values()){
                            if(!jvmExecutor.jvmServices.isEmpty()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    service.kill();
                                }
                            }

                        }

                        for(JVMExecutor jvmExecutor : instance.getJvmContainer().jvmExecutorsServers.values()){
                            if(!jvmExecutor.jvmServices.isEmpty()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    service.kill();
                                }
                            }
                        }
                        isReady = true;
                        Console.debugPrint("\n"+Chalk.on("DreamNetwork process shutdown, please wait...").bgMagenta().bold().underline().white());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Console.debugPrint("\n"+Chalk.on("DreamNetwork process shutdown, please wait...").bgMagenta().bold().underline().white());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    Console.debugPrint(e.getMessage());
                    e.printStackTrace(instance.formatter.getDefaultStream());
                }

            }
        });




        Console.load("m:default").isRunning = true;


        instance = new Client();

        new TemplateLoading();

    }

    public static void start(){
        instance = new Client();
    }

    public Client(){

        //JVM ARGUMENTS
        String s = System.getProperty("ebug");
        if(s != null && s.equalsIgnoreCase("true")){
            System.out.println(Chalk.on("DEBUG MODE ENABLED !").bgGreen());
            debug = true;

        }

        FileHandler fh = null;
        try {
            fh = new FileHandler("latest.log");
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
        try {
            console.fPrint("Ça démarre tkt",Level.INFO);
            Thread thread = new Thread(new CoreServer(8080));
            thread.start();
            console.fPrint("C'est démarré frr",Level.INFO);
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

        CommandReader commandReader = new CommandReader(console);
        ScreenManager.load();



        //MANAGER
        this.clientManager = new ClientManager(this);



    }





}
