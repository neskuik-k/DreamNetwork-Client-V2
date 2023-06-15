package be.alexandre01.dreamnetwork.core.installer;

import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.config.EstablishedAction;
import be.alexandre01.dreamnetwork.core.config.FileCopyAsync;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.installer.enums.InstallationLinks;
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
    public static ArrayList<ContentInstaller.IInstall> queue = new ArrayList<>();
    @SneakyThrows
    public static boolean launchDependInstall(String version, File file, ContentInstaller.IInstall iInstall){
        if(!queueisAvailable){
            queue.add(new ContentInstaller.IInstall() {
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
            Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("installer.incorrectLink"));
        }

        return false;
    }
    @SneakyThrows
    public static boolean launchMultipleInstallation(String url, List<File> files, String name, ContentInstaller.IInstall iInstall){
        if(!queueisAvailable){
            queue.add(new ContentInstaller.IInstall() {
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
            install(url, stream, name, new ContentInstaller.IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    for (int i = 1; i < files.size(); i++) {
                        try {
                            Config.asyncCopy(files.get(0), files.get(i), new FileCopyAsync.ICallback() {
                                @Override
                                public void call() {

                                }

                                @Override
                                public void cancel() {

                                }
                            },false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(iInstall != null)
                        iInstall.complete();
                }
            });
        }catch (Exception e){
            Console.setBlockConsole(false);
            Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("installer.installationCantBeLaunched"));
        }

        return false;
    }
    @SneakyThrows
    public static boolean launchInstallation(String url, File file,String name){
        if(!queueisAvailable){
            queue.add(new ContentInstaller.IInstall() {
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
            Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("installer.installationCantBeLaunched"));
        }

        return false;
    }
    private static void install(String url, FileOutputStream stream, String name){
        install(url,stream,name,null);
    }
    private static void install(String url, FileOutputStream stream, String name, ContentInstaller.IInstall iInstall){
        try {
            AsyncHttpClient client = Dsl.asyncHttpClient();

            AtomicBoolean completed = new AtomicBoolean(false);
            queueisAvailable = false;
            if(iInstall != null)
                iInstall.start();
            Console.printLang("installer.startInstallation");
            Core.getInstance().formatter.getDefaultStream().flush();
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


                        sb.append(Colors.RESET+Colors.ANSI_YELLOW+"]"+Colors.ANSI_RESET());
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
                    Core.getInstance().formatter.getDefaultStream().print(Console.getFromLang("installer.progress", name,sb.toString(), stream.getChannel().size()/(1024*1024), space.toString()));
                    Core.getInstance().formatter.getDefaultStream().flush();
                    return State.CONTINUE;
                }

                @Override
                public FileOutputStream onCompleted(Response response) {
                    try {

                        ConsoleReader.sReader.printAbove("\n");
                        Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("installer.completed", String.valueOf(stream.getChannel().size()/1024)));

                        if(iInstall != null){
                            iInstall.complete();
                        }


                        if(!queue.isEmpty()){
                            ContentInstaller.IInstall i = queue.get(0);
                            queue.remove(0);
                           // Core.getInstance().formatter.getDefaultStream().println(Console.getFromLang("installer.emptyQueue"));
                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                            scheduler.scheduleAtFixedRate(new Runnable() {
                                @Override
                                public void run() {
                                    queueisAvailable = true;
                                    i.complete();
                                    Core.getInstance().formatter.getDefaultStream().println("SKIPPING QUEUE AND WAITING");
                                    scheduler.shutdown();
                                }},500,1,TimeUnit.MILLISECONDS);


                            try {
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                            }
                            return stream;
                        }
                        queueisAvailable = true;
                        try {

                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                        }
                    }catch (Exception e){
                        e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                        return stream;
                    }

                    return stream;
                }
            });
        }catch (Exception e){
            e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
        }
    }





}
