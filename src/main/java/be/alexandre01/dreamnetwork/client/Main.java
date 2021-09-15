package be.alexandre01.dreamnetwork.client;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.tomaslanger.chalk.Chalk;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.slf4j.LoggerFactory;

import be.alexandre01.dreamnetwork.client.api.DNAPI;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.config.SecretFile;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.libraries.LoadLibraries;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import lombok.Getter;


public class Main {
    @Getter
    public static Client instance;
    @Getter
    private JVMContainer jvmContainer;
    @Getter
    private SpigetConsole spigetConsole;
    @Getter
    private static String username;
    @Getter
    private static boolean disabling = false;
    @Getter
    private static boolean license;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Console.clearConsole();

        DNAPI dnapi = new DNAPI();
        PrintStream outputStream = System.out;
        
        new LoadLibraries().init();
        //UTF8
        Chalk.setColorEnabled(true);
        System.out.println(Colors.RESET);
        Logger.getGlobal().setLevel(Level.WARNING);
        System.setProperty("file.encoding","UTF-8");
        Logger.getLogger("jdk.event.security").setLevel(Level.OFF);
        Logger.getLogger("io.netty.util.internal.logging.InternalLoggerFactory").setLevel(Level.OFF);
        Logger.getLogger("jdk.internal.event.EventHelper").setLevel(Level.OFF);
        String pathSlf4J = Client.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "log4j.properties";
        if(!Config.isWindows()){
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.FINE);
            Logger.getLogger("").addHandler(ch);
            Logger.getLogger("").setLevel(Level.FINE);
        }


        try{
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null,null);
        }catch (Exception e){
            //JDK16 COMPATIBILITY
            String version = System.getProperty("java.version");
            NumberFormat format = NumberFormat.getInstance();
            double v = 0;
            try {
                v = format.parse(version.split("_")[0]).doubleValue();
                if(v <= 1.15d){
                    e.printStackTrace();
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }

        if(Config.isWindows()){
            Client.setUsername(username = System.getProperty("user.name"));
        }else {
            try {
                Client.setUsername( username = InetAddress.getLocalHost().getHostName());

            } catch (UnknownHostException e) {
                Client.setUsername(username = System.getProperty("user.name"));
            };
        }
        boolean l = false;

            SecretFile secretFile = null;
            try {
                secretFile = new SecretFile();
                secretFile.init();



            } catch (IOException e) {
                e.printStackTrace();
            }


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    disabling = true;
                    if(instance != null){
                        boolean isReady = false;
                        for(JVMExecutor jvmExecutor : instance.getJvmContainer().jvmExecutorsProxy.values()){
                            if(!jvmExecutor.jvmServices.isEmpty()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    //service.kill();
                                }
                            }

                        }

                        for(JVMExecutor jvmExecutor : instance.getJvmContainer().jvmExecutorsServers.values()){
                            if(!jvmExecutor.jvmServices.isEmpty()){
                                for(JVMService service : jvmExecutor.getServices()){
                                    //service.kill();
                                }
                            }
                        }
                        isReady = true;
                        outputStream.println("\n"+Chalk.on("DreamNetwork process shutdown, please wait..."+Colors.RESET).bgMagenta().bold().underline().white());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        outputStream.println("\n"+Chalk.on("DreamNetwork process shutdown, please wait..."+Colors.RESET).bgMagenta().bold().underline().white());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }

            }
        });

        try {
            if(!dnapi.hasValidLicense(secretFile.getUuid(),secretFile.getSecret())){
                System.out.println(Colors.RED+ "The license key is invalid!");
                secretFile.deleteSecretFile();
                System.exit(1);
                return;
            }
            System.out.println(Colors.PURPLE+"Successfully authenticated !\n"+Colors.RESET);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadClient();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public static void loadClient(){
        Console.load("m:default").isRunning = true;


        instance = new Client();
        Client.instance = instance;


        new TemplateLoading();
    }
}
