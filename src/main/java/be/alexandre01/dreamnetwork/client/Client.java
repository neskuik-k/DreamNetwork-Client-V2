package be.alexandre01.dreamnetwork.client;


import be.alexandre01.dreamnetwork.client.commands.CommandReader;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.formatter.ConciseFormatter;
import be.alexandre01.dreamnetwork.client.console.formatter.Formatter;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.utils.ASCIIART;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;
import lombok.Setter;


import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    @Getter boolean debug = false;
    private InputStream in;
    public Formatter formatter;
    @Getter @Setter
    public static Logger logger = Logger.getLogger(Client.class.getName());
    @Getter
    public static Client instance;
    @Getter
    private JVMContainer jvmContainer;
    @Getter
    private SpigetConsole spigetConsole;



    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
     
        //UTF8
        System.setProperty("file.encoding","UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);
        Chalk.setColorEnabled(true);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Console.debugPrint("\n"+Chalk.on("DreamNetwork process shutdown, please wait...").bgMagenta().bold().underline().white());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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


        System.out.println(Chalk.on("Le Network a été démarré avec succès / Faites help pour avoir les commandes").green());
        Console console = Console.getConsole("m:default");
        CommandReader commandReader = new CommandReader(console);


        //MANAGER



    }





}
