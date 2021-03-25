package be.alexandre01.dreamnetwork.client.installer;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Config;
import be.alexandre01.dreamnetwork.client.commands.CommandsManager;
import be.alexandre01.dreamnetwork.client.commands.lists.HelpCommand;
import be.alexandre01.dreamnetwork.client.commands.lists.ServiceCommand;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.utils.spiget.Ressource;
import be.alexandre01.dreamnetwork.utils.spiget.exceptions.SearchRessourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SpigetConsole {
    Console console;
    private ArrayList<Ressource> ressourcesFind;
    private ArrayList<Ressource> ressourcesSelected;
    private ArrayList<JVMExecutor> serverSelected;
    public SpigetConsole(Console console){
        this.console = console;
        console.writing = "- ";
        sendHelp();
        run();
    }

    public void run(){
        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
                try {
                    if(args.length < 1){
                        sendHelp();
                    }
                    if(!args[0].equalsIgnoreCase(" ")){
                        if(args[0].equalsIgnoreCase("EXIT")){
                            Console.setActualConsole(Console.defaultConsole);
                            return;
                        }
                        if(args[0].equalsIgnoreCase("SEARCH") || args[0].equalsIgnoreCase("SRH")){
                            if(args.length < 3){
                                console.fPrint("- SEARCH [VALUE] [NAME/TAG/AUTHORS] [PAGE]", Level.INFO);
                            }
                            int p;
                            try {
                                p = Integer.parseInt(args[3]);
                            }catch (Exception e){
                                console.fPrint("- SEARCH PAGE INVALID", Level.INFO);
                                return;
                            }
                            try {
                                ArrayList<Ressource> r = Ressource.searchRessources(args[1],p,5,1, Ressource.Field.valueOf(args[2].toUpperCase()));
                                int i = 1;
                                for(Ressource ressource : r){
                                    ressourcesFind.add(ressource);
                                    console.fPrint("["+i+"] - "+ Colors.CYAN+ressource.getName()+Colors.RESET,Level.INFO);
                                    console.fPrint(""+ressource.getTag(),Level.INFO);
                                    i++;
                                }
                            } catch (SearchRessourceException e) {
                                console.fPrint("- SEARCH PROBLEM", Level.INFO);
                                e.printStackTrace(Client.getInstance().formatter.prStr);
                            }
                            return;
                        }

                        if(args[0].equalsIgnoreCase("SELECT") || args[0].equalsIgnoreCase("SLC")){
                            if(args.length < 2){
                                console.fPrint("- SELECT ADD [ID/URL]", Level.INFO);
                                console.fPrint("- SELECT REMOVE [ID_NUM]", Level.INFO);
                                console.fPrint("- SELECT LIST", Level.INFO);
                            }

                            if(args[1].equalsIgnoreCase("LIST")){
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
                            }
                        }
                        sendHelp();
                    }
                }catch (Exception e){
                    console.forcePrint(e.getMessage(),Level.SEVERE);
                    e.printStackTrace(Client.getInstance().formatter.prStr);
                }

            }
        });

        if(!Config.isWindows()){
            write("- ");
        }


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
                    Client.getInstance().formatter.getDefaultStream().write(stringToBytesASCII(str));
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
            return true;
        }
        return false;
    }

    private boolean isHttps(String s){
        if(s == null){
            return false;
        }
        try {
            int i = Integer.parseInt(s);
        }catch (Exception e){
            return true;
        }
        return false;
    }
}
