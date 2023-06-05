package be.alexandre01.dreamnetwork.core.installer;

import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.utils.spiget.Ressource;
import org.jline.reader.LineReader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SpigetConsole {
    Console console;
    private ArrayList<Ressource> ressourcesFind;
    private ArrayList<Ressource> ressourcesSelected;
    private ArrayList<IJVMExecutor> serverSelected;
    public SpigetConsole(Console console){
        this.console = console;
        ressourcesFind = new ArrayList<>();
        ressourcesSelected = new ArrayList<>();
        serverSelected = new ArrayList<>();
        ressourcesSelected = new ArrayList<>();
        serverSelected = new ArrayList<>();
        console.writing = "- ";
        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public boolean onKill(LineReader reader) {
                Console.setActualConsole("m:default");
                return true;
            }
        });


        run();
    }

    public void run(){


        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {

                    if(args.length < 1){
                        sendHelp();
                    }
                    if(!args[0].equalsIgnoreCase(" ")){
                        if(args[0].equalsIgnoreCase("EXIT")){
                            Console.setActualConsole(Console.defaultConsole);
                            return;
                        }
                        if(args[0].equalsIgnoreCase("DOWNLOAD") || args[0].equalsIgnoreCase("DWN")){
                            if(ressourcesSelected.isEmpty()){
                                console.fPrint("- SELECTED PLUGINS EMPTY", Level.INFO);
                                return;
                            }
                            if(serverSelected.isEmpty()){
                                console.fPrint("- SELECTED GROUP EMPTY", Level.INFO);
                                return;
                            }
                            List<File> dirs = new ArrayList<>();
                            for (IJVMExecutor jvmExecutor : serverSelected){
                                Config.createDir(Config.getPath(jvmExecutor.getFileRootDir().getAbsolutePath()+"/plugins/"));
                                dirs.add(new File(Config.getPath(jvmExecutor.getFileRootDir().getAbsolutePath()+"/plugins/")));
                            }
                            for (Ressource ressource : ressourcesSelected){
                                URL url = null;

                                try {

                                    console.fPrint(ressource.getDwnLink(),Level.FINE);
                                    console.fPrint(ressource.getFileName(),Level.FINE);

                                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


                                    URL finalUrl = url;
                                    scheduler.scheduleAtFixedRate(new Runnable() {

                                        @Override
                                        public void run() {
                                            Installer.launchMultipleInstallation(ressource.getDwnLink(), dirs, ressource.getFileName(), new ContentInstaller.IInstall() {
                                                @Override
                                                public void start() {

                                                }

                                                @Override
                                                public void complete() {
                                                    try {
                                                        HttpURLConnection con = (HttpURLConnection) finalUrl.openConnection();
                                                        con.setRequestMethod("GET");
                                                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                                        con.connect();
                                                    }catch (Exception e){

                                                    }
                                                }
                                            });
                                            scheduler.shutdown();
                                        }
                                },1,1,TimeUnit.SECONDS);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }


                        }
                        if(args[0].equalsIgnoreCase("SEARCH") || args[0].equalsIgnoreCase("SRH")){
                            if(args.length < 3){
                                console.fPrint("- SEARCH [VALUE] [NAME/TAG/AUTHORS] [PAGE] (-ver=1.8)", Level.INFO);
                            }
                            int p;
                            try {
                                p = Integer.parseInt(args[3]);
                            }catch (Exception e){
                                console.fPrint("- SEARCH PAGE INVALID", Level.INFO);
                                return;
                            }
                            int s = 5;
                            if(args.length > 4){
                                if(args[4] != null){
                                    s = 20;
                                }
                            }

                            try {
                                ArrayList<Ressource> r = Ressource.searchRessources(args[1],p,s,1, Ressource.Field.valueOf(args[2].toUpperCase()));
                                int i = 1;
                                ressourcesFind.clear();
                                for(Ressource ressource : r){
                                    System.out.println(r);
                                    if(args.length > 4){
                                        if(args[4] != null){
                                            if(!ressource.getTestedVersions().contains(args[4])){
                                                continue;
                                            }
                                        }
                                    }
                                    if(ressource.isPremium()){
                                        continue;
                                    }
                                    URL url = new URL("https://api.spiget.org/v2/resources/"+ressource.getId()+"/download");
                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                    con.setRequestMethod("GET");
                                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");



                                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                    con.connect();

                                    boolean isAccepted = true;



                                    String fieldValue = con.getHeaderField("x-bz-file-name");
                                    if(fieldValue == null){
                                        isAccepted = false;
                                    }else {
                                        String[] splitted = fieldValue.split("\\.");

                                        String filename = ressource.getName()+"."+ splitted[splitted.length-1];
                                        ressource.setFileName(Normalizer.normalize(filename, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ",""));
                                    }

                                    //ANCIEN A GARDER
                                   /* String fieldValue = con.getHeaderField("Content-Disposition");
                                    if (fieldValue == null || !fieldValue.contains("filename=\"")){
                                        isAccepted = false;
                                    }else {
                                        String filename = fieldValue.substring(fieldValue.indexOf("filename=\"") + 10, fieldValue.length() - 1);
                                        ressource.setFileName(Normalizer.normalize(filename, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ",""));
                                    }*/
                                    Core.getInstance().formatter.getDefaultStream().println(con.getResponseCode());
                                    console.fPrint(con.getResponseCode(),Level.FINE);
                                    if(con.getResponseCode() != 200){
                                        isAccepted = false;
                                        if(con.getResponseCode() == 307){
                                            isAccepted = false;
                                            for (String ss : con.getHeaderFields().keySet())
                                            {
                                                if(ss != null){
                                                    if (ss.equals("Location")) {
                                                        for(String sb : con.getHeaderFields().get(ss)){
                                                            ressource.setDwnLink(sb);
                                                            console.fPrint("LOC>>"+sb,Level.FINE);
                                                            isAccepted = true;

                                                            URL tUrl = new URL(sb);
                                                            HttpURLConnection tCon = (HttpURLConnection) tUrl.openConnection();
                                                            tCon.setRequestMethod("GET");
                                                            tCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");



                                                            tCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                                            tCon.connect();

                                                            String tFieldValue = con.getHeaderField("x-bz-file-name");
                                                            if(tFieldValue == null){
                                                                isAccepted = false;
                                                            }else {
                                                                String[] splitted = tFieldValue.split("\\.");

                                                                String filename = ressource.getName()+"."+ splitted[splitted.length-1];
                                                                ressource.setFileName(Normalizer.normalize(filename, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ",""));
                                                            }


                                                        }

                                                    }
                                                }

                                            }
                                    }
                                    }
                                    if(!isAccepted){
                                       continue;
                                    }
                                    String[] formatFiles = ressource.getFileName().split("\\.");
                                    String formatFile = formatFiles[formatFiles.length-1];
                                    console.fPrint("DWNLINK>>"+ressource.getDwnLink(),Level.FINE);
                                    ressourcesFind.add(ressource);
                                    console.fPrint("["+i+"] - "+ Colors.CYAN+ressource.getName()+"   "+ Colors.YELLOW+"["+Colors.WHITE+formatFile.toUpperCase()+" FORMAT"+Colors.YELLOW+"] "+Colors.RESET,Level.INFO);
                                    console.fPrint(""+ressource.getTag(),Level.INFO);
                                    i++;
                                }
                                if(i == 1){
                                    console.fPrint("- NO PLUGINS IN THIS CATEGORY OF SEARCH", Level.INFO);
                                }
                            } catch (Exception e) {
                                console.fPrint("- SEARCH PROBLEM", Level.INFO);
                                e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                            }
                            return;
                        }

                        if(args[0].equalsIgnoreCase("SELECT") || args[0].equalsIgnoreCase("SLC")){
                            if(args.length < 2){
                                console.fPrint("- SELECT ADD [ID/URL]", Level.INFO);
                                console.fPrint("- SELECT REMOVE [ID_NUM]", Level.INFO);
                                console.fPrint("- SELECT LIST", Level.INFO);
                                return;
                            }

                            if(args[1].equalsIgnoreCase("ADD")){
                                if(args.length > 2){
                                    if(isNumber(args[2])){
                                        Ressource ressource = null;
                                        try {
                                            ressource = ressourcesFind.get(Integer.parseInt(args[2])-1);
                                        }catch (Exception ignored){

                                        }

                                        if(ressource != null){
                                            console.fPrint(Colors.ANSI_CYAN+ressource.getName()+Colors.ANSI_RESET()+" ADDED", Level.INFO);
                                            ressourcesSelected.add(ressource);
                                            return;
                                        }
                                        console.fPrint("THE ID IS INVALID", Level.INFO);
                                        return;
                                    }
                                }else {
                                    console.fPrint("- SELECT ADD [ID/URL]", Level.INFO);
                                }

                                return;
                            }
                            if(args[1].equalsIgnoreCase("RMV")){
                                if(args.length > 2){
                                    if(isNumber(args[2])){
                                        Ressource ressource = null;
                                        try {
                                            ressource = ressourcesSelected.get(Integer.parseInt(args[2])-1);
                                        }catch (Exception ignored){

                                        }

                                        if(ressource != null){
                                            console.fPrint(Colors.ANSI_CYAN+ressource.getName()+Colors.ANSI_RESET()+" REMOVED !", Level.INFO);
                                            ressourcesSelected.remove(ressource);
                                            return;
                                        }
                                        console.fPrint("THE ID IS INVALID", Level.INFO);
                                        return;
                                    }
                                }else {
                                    console.fPrint("- SELECT REMOVE [ID/URL]", Level.INFO);
                                }

                                return;
                            }
                            if(args[1].equalsIgnoreCase("LIST")){
                                if(!ressourcesSelected.isEmpty()){
                                    int i = 1;
                                    for (Ressource ressource : ressourcesSelected){
                                        console.fPrint("["+i+"] - "+ Colors.CYAN+ressource.getName()+Colors.RESET,Level.INFO);
                                        console.fPrint(""+ressource.getTag(),Level.INFO);
                                        i++;
                                    }
                                    return;
                                }

                                console.fPrint("THE SELECTED LIST IS EMPTY", Level.INFO);
                                return;
                            }
                        }

                        if(args[0].equalsIgnoreCase("GROUP") || args[0].equalsIgnoreCase("GRP")){
                            if(args.length < 2){
                                console.fPrint("- GROUP ADD PROXY [NAME]", Level.INFO);
                                console.fPrint("- GROUP ADD SERVER [NAME]", Level.INFO);
                                console.fPrint("- GROUP LIST", Level.INFO);
                                console.fPrint("- GROUP RMV PROXY [NAME]", Level.INFO);
                                console.fPrint("- GROUP RMV SERVER [NAME]", Level.INFO);
                                return;
                            }
                            if(args[1].equalsIgnoreCase("ADD")){
                                if(args.length > 3){
                                    if(addServer(args[3],args[2])){
                                        console.fPrint("THE SERVER HAS BEEN ADDED!", Level.INFO);
                                        return;
                                    }
                                    console.fPrint("THE SERVER CANNOT BE ADDED!", Level.INFO);
                                    return;
                                }
                                console.fPrint("- GROUP ADD PROXY [NAME]", Level.INFO);
                                console.fPrint("- GROUP ADD SERVER [NAME]", Level.INFO);
                            }
                            if(args[1].equalsIgnoreCase("RMV")){
                                if(args.length > 2){
                                    if(isNumber(args[2])){
                                        IJVMExecutor jvmExecutor = null;
                                        try {
                                            jvmExecutor = serverSelected.get(Integer.parseInt(args[2])-1);
                                        }catch (Exception ignored){

                                        }

                                        if(jvmExecutor != null){
                                            console.fPrint(Colors.ANSI_CYAN+jvmExecutor.getName()+Colors.ANSI_RESET()+" REMOVED !", Level.INFO);
                                            serverSelected.remove(jvmExecutor);
                                            return;
                                        }
                                        console.fPrint("THE ID IS INVALID", Level.INFO);
                                        return;
                                    }
                                }else {
                                    console.fPrint("- SELECT ADD [ID/URL]", Level.INFO);
                                }

                                return;
                            }
                            if(args[1].equalsIgnoreCase("LIST")){
                                if(!serverSelected.isEmpty()){
                                    int i = 1;
                                    for (IJVMExecutor jvmExecutor : serverSelected){
                                        console.fPrint("["+i+"] - "+ Colors.CYAN+jvmExecutor.getName()+Colors.RESET+"\n",Level.INFO);
                                        i++;
                                    }
                                    return;
                                }

                                console.fPrint("THE GROUP LIST IS EMPTY", Level.INFO);
                                return;
                            }

                        }
                        sendHelp();
                    }


            }

            @Override
            public void consoleChange() {
                    sendHelp();
            }
        });

        if(!Config.isWindows()){
            write("- ");
        }


    }
    private boolean addServer(String name, String bundle){
        BundleData bundleData = Main.getBundleManager().getBundleData(bundle);
        IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(name,bundleData);
        if(jvmExecutor != null){
            serverSelected.add(jvmExecutor);
            return true;
        }
        return false;
    }
    private void sendHelp(){
        console.fPrint("HELP SPIGET:",Level.INFO);
        console.fPrint("- DOWNLOAD", Level.INFO);
        console.fPrint("- SEARCH [VALUE] [NAME/TAG/AUTHORS] [PAGE]", Level.INFO);
        console.fPrint("- SELECT [ID/URL]",Level.INFO);
        console.fPrint("- EXIT",Level.INFO);
    }

    public void write(String str){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Core.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
                    scheduler.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },50,50, TimeUnit.MILLISECONDS);

    }
    public static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }

    private boolean isNumber(String s){
        if(s == null){
            return false;
        }
        try {
            int i = Integer.parseInt(s);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean isHttps(String s){
        if(s.toLowerCase().startsWith("https://")){
            return true;
        }
        return false;
    }
}
