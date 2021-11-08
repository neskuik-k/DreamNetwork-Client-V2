package be.alexandre01.dreamnetwork.client.installer;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.config.EstablishedAction;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.installer.enums.InstallationLinks;
import lombok.SneakyThrows;
import org.asynchttpclient.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Installer {
    public static boolean queueisAvailable = true;
    public static ArrayList<IInstall> queue = new ArrayList<>();
    @SneakyThrows
    public static boolean launchDependInstall(String version, File file, IInstall iInstall){
        if(!queueisAvailable){
            queue.add(new IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    launchDependInstall(version,file,iInstall);
                }
            });
            return true;
        }
        InstallationLinks installationLinks = InstallationLinks.getInstallationLinks(version);
        FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"/"+ installationLinks.name().toLowerCase()+".jar");
        try {
            install(installationLinks.getUrl(),stream,installationLinks.name(),iInstall);
        }catch (Exception e){
            Client.getInstance().formatter.getDefaultStream().println(Colors.RED+"THE INSTALLATION LINKS IS INCORRECT.");
        }

        return false;
    }
    @SneakyThrows
    public static boolean launchMultipleInstallation(String url, List<File> files, String name, IInstall iInstall){
        if(!queueisAvailable){
            queue.add(new IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    launchMultipleInstallation(url,files,name,iInstall);
                }
            });
            return true;
        }
        FileOutputStream stream = new FileOutputStream(files.get(0).getAbsolutePath()+"/"+ name);
        try {
            install(url, stream, name, new IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    for (int i = 1; i < files.size(); i++) {
                        try {
                            Config.asyncCopy(files.get(0), files.get(i), new EstablishedAction() {
                                @Override
                                public void completed() {

                                }

                                @Override
                                public void cancelled() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(iInstall != null)
                        iInstall.complete();
                }
            });
        }catch (Exception e){
            Client.getInstance().formatter.getDefaultStream().println(Colors.RED+"THE INSTALLATION CANNOT BE LAUNCHED.");
        }

        return false;
    }
    @SneakyThrows
    public static boolean launchInstallation(String url, File file,String name){
        if(!queueisAvailable){
            queue.add(new IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    launchInstallation(url,file,name);
                }
            });
            return true;
        }
        FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"/"+ name);
        try {
            install(url,stream,name);
        }catch (Exception e){
            Client.getInstance().formatter.getDefaultStream().println(Colors.RED+"THE INSTALLATION CANNOT BE LAUNCHED.");
        }

        return false;
    }
    public static void loadBar(){

    }
    private static void install(String url, FileOutputStream stream, String name){
        install(url,stream,name,null);
    }
    private static void install(String url, FileOutputStream stream, String name, IInstall iInstall){
            try {
                AsyncHttpClient client = Dsl.asyncHttpClient();

                AtomicBoolean completed = new AtomicBoolean(false);
                queueisAvailable = false;
                if(iInstall != null)
                iInstall.start();
                Console.print("Starting installation...");
                Client.getInstance().formatter.getDefaultStream().flush();
                ListenableFuture<?> l = client.prepareGet(url).execute(new AsyncCompletionHandler<FileOutputStream>() {
                    String bar = "<->";
                    int space = 30;
                    int calc = 0;
                    int slower = 30;
                    boolean directionRight = true;
                    StringBuilder sb = new StringBuilder();
                    @Override
                    public AsyncHandler.State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                            throws Exception {
                        stream.getChannel().write(bodyPart.getBodyByteBuffer());

                        if(calc == 0 || calc % slower == 0){
                            int currentSpace = space;
                            sb = new StringBuilder();
                            sb.append(Colors.ANSI_YELLOW+Colors.BLACK_BACKGROUND+"["+Colors.ANSI_RESET()+Colors.WHITE_BACKGROUND_BRIGHT);
                            for (int i = 0; i < calc/slower; i++) {
                                currentSpace--;
                                sb.append(" ");
                            }
                            sb.append(Colors.ANSI_BLACK()+Colors.WHITE_BACKGROUND+bar+Colors.ANSI_RESET()+Colors.WHITE_BACKGROUND_BRIGHT);

                            for (int i = 0; i < currentSpace; i++) {
                                sb.append(" ");
                            }


                            sb.append(Colors.ANSI_YELLOW+Colors.BLACK_BACKGROUND+"]"+Colors.ANSI_RESET());
                        }
                        if(calc <= space*slower && directionRight){
                            calc++;
                            if(calc == space*slower){
                                directionRight = false;
                            }
                        }
                        if(calc >= 0 && !directionRight){
                            calc--;
                            if(calc == 0){
                                directionRight = true;
                            }
                        }
                        StringBuilder space = new StringBuilder();
                        for (int i = 0; i < 35; i++) {
                            space.append(" ");
                        }
                        Client.getInstance().formatter.getDefaultStream().print("Installation of "+ Colors.ANSI_CYAN+ name+Colors.ANSI_RESET()+"   "+ sb.toString()+ " ["+stream.getChannel().size()/(1024*1024)+"mb]"+space.toString()+"\r");
                        Client.getInstance().formatter.getDefaultStream().flush();
                        return State.CONTINUE;
                    }

                    @Override
                    public FileOutputStream onCompleted(Response response) {
                        try {

                            ConsoleReader.sReader.printAbove("\n");
                            Client.getInstance().formatter.getDefaultStream().println("\nCOMPLETE... "+ stream.getChannel().size()/1024 +"kb in total");

                            if(iInstall != null){
                                iInstall.complete();
                            }


                            if(!queue.isEmpty()){
                                IInstall i = queue.get(0);
                                queue.remove(0);
                                Client.getInstance().formatter.getDefaultStream().println("!QUEUE EMPTY");
                                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                                scheduler.scheduleAtFixedRate(new Runnable() {
                                    @Override
                                    public void run() {
                                        queueisAvailable = true;
                                        i.complete();
                                        Client.getInstance().formatter.getDefaultStream().println(" COMP");
                                        scheduler.shutdown();
                                    }},1000,1,TimeUnit.MILLISECONDS);


                                try {
                                    client.close();
                                } catch (IOException e) {
                                    e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
                                }
                                return stream;
                            }
                            queueisAvailable = true;
                            try {

                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
                            }
                        }catch (Exception e){
                            e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
                            return stream;
                        }

                        return stream;
                    }
                });
            }catch (Exception e){
                e.printStackTrace(Client.getInstance().formatter.getDefaultStream());
            }




    }

        public interface IInstall{
        public void start();
        public void complete();
        }



}
