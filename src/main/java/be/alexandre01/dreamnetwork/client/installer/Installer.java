package be.alexandre01.dreamnetwork.client.installer;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.installer.enums.InstallationLinks;
import lombok.SneakyThrows;
import org.asynchttpclient.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class Installer {
    @SneakyThrows
    public static boolean launchInstall(String version, File file){
        System.out.println(version);
        System.out.println(InstallationLinks.getInstallationLinks(version).getUrl());
        InstallationLinks installationLinks = InstallationLinks.getInstallationLinks(version);
        if(installationLinks != null){
            AsyncHttpClient client = Dsl.asyncHttpClient();
            System.out.println("ok");
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getPath());
            FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"/"+ installationLinks.name().toLowerCase()+".jar");
            System.out.println(stream);
            System.out.println(InstallationLinks.getInstallationLinks(version));
            AtomicBoolean completed = new AtomicBoolean(false);

            ListenableFuture<?> l = client.prepareGet(installationLinks.getUrl()).execute(new AsyncCompletionHandler<FileOutputStream>() {
                String bar = "<->";
                int space = 30;
                int calc = 0;
                int slower = 30;
                boolean directionRight = true;
                StringBuilder sb = new StringBuilder();
                @Override
                public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
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
                    Client.getInstance().formatter.getDefaultStream().print("Installation of "+ Colors.ANSI_CYAN+ installationLinks.name()+Colors.ANSI_RESET()+"   "+ sb.toString()+ " ["+stream.getChannel().size()/(1024*1024)+"mb]\r");
                    return State.CONTINUE;
                }

                @Override
                public FileOutputStream onCompleted(Response response)
                        throws Exception {
                    Client.getInstance().formatter.getDefaultStream().print("\n");
                    System.out.println("\nCOMPLETE...");

                    return stream;
                }
            });

            System.out.println("close");

        }

        return false;
    }

    public static void loadBar(){

    }
}
