package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Config;
import be.alexandre01.dreamnetwork.client.commands.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;

import java.io.BufferedWriter;


import java.util.Arrays;
import java.util.logging.Level;

public class Add implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(String[] args) {

        BufferedWriter processInput = null;
        if(args[0].equalsIgnoreCase("add")){
            if(args.length >= 5){

                 if(args[1].equalsIgnoreCase("server")||args[1].equalsIgnoreCase("proxy")){
                     boolean proxy = args[1].equalsIgnoreCase("proxy");
                     System.out.println(args[2]);
                     JVMContainer.JVMType jvmType;
                     try {
                         jvmType = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
                     }catch (Exception e){
                         System.out.println(Chalk.on("[!] The type is incorrect... try PROXY or SERVER"));
                         return true;
                     }
                         if(args[3].equalsIgnoreCase("STATIC")){
                             Config.createDir("template/"+args[1]+"/"+args[2]);
                             if(args.length == 7){
                                 try {
                                     JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                     if(jvmExecutor == null){
                                         jvmExecutor = new JVMExecutor(args[1],args[2],JVMExecutor.Mods.STATIC,args[4],args[5],Integer.parseInt(args[6]),proxy,true);
                                         Console.print(Colors.ANSI_GREEN()+"Vous venez de configurer le serveur avec succès !");
                                         return true;
                                     }

                                     jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.STATIC,args[4],args[5],Integer.parseInt(args[6]),proxy,null,null);
                                     Console.print("Vous venez de configurer le serveur avec succès !");
                                 }catch (Exception e){
                                     Console.print(Colors.ANSI_RED()+"Une erreur c'est produite, certainement car vous avez mal noté le port", Level.SEVERE);
                                 }
                             }else {
                                 try {
                                     //ServerInstance.updateConfigFile(args[1],args[2], Mods.STATIC,args[4],args[5],0,proxy);
                                     JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                     if(jvmExecutor == null){
                                         jvmExecutor = new JVMExecutor(args[1],args[2],JVMExecutor.Mods.STATIC,args[4],args[5],0,proxy,true);
                                         Console.print(Colors.ANSI_GREEN()+"Vous venez de configurer le serveur avec succès !");
                                         return true;
                                     }

                                     jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.STATIC,args[4],args[5],0,proxy,null,null);
                                     Console.print(Colors.ANSI_GREEN()+"Vous venez de re-configurer le serveur avec succès !");
                                 } catch (Exception e) {
                                     Console.print(Colors.ANSI_RED()+"Une erreur c'est produite lors de l'update du fichier.", Level.SEVERE);
                                 }
                             }

                         }else {
                             if(args[3].equalsIgnoreCase("DYNAMIC")){
                                 Config.createDir("template/"+args[1]+"/"+args[2]);
                                 if(args.length == 7){
                                     try {
                                         JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                         if(jvmExecutor == null){
                                             jvmExecutor = new JVMExecutor(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],Integer.parseInt(args[6]),proxy,true);
                                             Console.print(Colors.ANSI_GREEN()+"Vous venez de configurer le serveur avec succès !");
                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],Integer.parseInt(args[6]),proxy,null,null);
                                         Console.print(Colors.ANSI_GREEN()+"Vous venez de configurer le serveur avec succès !");
                                     }catch (Exception e){
                                         Console.print(Colors.ANSI_RED()+"Une erreur c'est produite, certainement car vous avez mal noté le port", Level.SEVERE);
                                     }

                                 }else {
                                     try {
                                         JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                         if(jvmExecutor == null){
                                             jvmExecutor = new JVMExecutor(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],0,proxy,true);
                                             Console.print(Colors.ANSI_GREEN()+"Vous venez de configurer le serveur avec succès !");
                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],0,proxy,null,null);

                                         Console.print(Colors.ANSI_GREEN()+"Vous venez de re-configurer le serveur avec succès !");
                                     }catch (Exception e){
                                         Console.print(Colors.ANSI_RED()+"Une erreur c'est produite lors de l'update du fichier.", Level.SEVERE);
                                     }
                                 }
                             }else {
                                 Console.print(Colors.ANSI_RED()+"[!] service add server [name] [DYNAMIC/STATIC] [XMS] [XMX] (PORT) => add a server ", Level.INFO);
                                 Console.print(Colors.ANSI_RED()+"[!] service add proxy [name] [DYNAMIC/STATIC] [XMS] [XMX] (PORT) => add a server ", Level.INFO);
                             }
                         }


                     }
                 }else {
                     Console.print(Colors.ANSI_RED()+"[!] service add server [name] [DYNAMIC/STATIC] [XMS] [XMX] (PORT) => add a server ", Level.INFO);
                     Console.print(Colors.ANSI_RED()+"[!] service add proxy [name] [DYNAMIC/STATIC] [XMS] [XMX] (PORT) => add a server ", Level.INFO);
                 }
                return true;
            }
            return false;
        }


    public String getStringArgs(String[] args){
        String stringArray[] = args;
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < stringArray.length; i++) {
            sb.append(stringArray[i]);
        }
        return Arrays.toString(stringArray);
    }


    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }

}