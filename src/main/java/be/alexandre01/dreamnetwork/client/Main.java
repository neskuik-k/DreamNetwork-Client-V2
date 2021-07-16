package be.alexandre01.dreamnetwork.client;

import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.installer.SpigetConsole;
import be.alexandre01.dreamnetwork.client.libraries.LoadLibraries;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import com.github.tomaslanger.chalk.Chalk;
import lombok.Getter;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logger.getLogger("jdk.event.security").setLevel(Level.OFF);
        Logger.getLogger("io.netty.util.internal.logging.InternalLoggerFactory").setLevel(Level.OFF);
        Logger.getLogger("jdk.internal.event.EventHelper").setLevel(Level.OFF);
        String pathSlf4J = Client.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "log4j.properties";
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        Logger.getLogger("").addHandler(ch);
        Logger.getLogger("").setLevel(Level.FINE);


        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);

        if(Config.isWindows()){
            Client.setUsername(username = System.getProperty("user.name"));
            System.out.println(username);
        }else {
            try {
                Client.setUsername( username = InetAddress.getLocalHost().getHostName());

            } catch (UnknownHostException e) {
                Client.setUsername(username = System.getProperty("user.name"));
            };
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
        Client.instance = instance;


        new TemplateLoading();

    }
}
