package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import org.jline.reader.impl.completer.NullCompleter;


import java.util.Arrays;
import java.util.logging.Level;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Add extends SubCommandCompletor implements SubCommandExecutor {
    public Add(){
        setCompletion(node("service",
                node("add",
                        node("server", "proxy",
                        node(NullCompleter.INSTANCE,
                        node("STATIC","DYNAMIC",
                        node("1G","2G")))))));
        addCompletor("service","add","server","","STATIC","1G","2G");
        addCompletor("service","add","server","","DYNAMIC","1G","2G");
        addCompletor("service","add","proxy","","STATIC","1G","2G");
        addCompletor("service","add","proxy","","DYNAMIC","1G","2G");
    }
    @Override
    public boolean onSubCommand(String[] args) {
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
                                         jvmExecutor.addConfigsFiles();
                                         Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                         return true;
                                     }
                                     jvmExecutor.addConfigsFiles();
                                     jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.STATIC,args[4],args[5],Integer.parseInt(args[6]),proxy,null,null,null);
                                     Console.print("You have successfully configured the server!");
                                 }catch (Exception e){
                                     e.printStackTrace();
                                     Console.print(Colors.ANSI_RED()+"An error occurred, probably because you wrote the port down wrong", Level.SEVERE);
                                 }
                             }else {
                                 try {
                                     //ServerInstance.updateConfigFile(args[1],args[2], Mods.STATIC,args[4],args[5],0,proxy);
                                     JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                     if(jvmExecutor == null){
                                         jvmExecutor = new JVMExecutor(args[1],args[2],JVMExecutor.Mods.STATIC,args[4],args[5],0,proxy,true);
                                         jvmExecutor.addConfigsFiles();
                                         Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                         return true;
                                     }
                                     jvmExecutor.addConfigsFiles();
                                     jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.STATIC,args[4],args[5],0,proxy,null,null,null);
                                     Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                     Console.print(Colors.ANSI_RED()+"An error occurred while updating the file.", Level.SEVERE);
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
                                             jvmExecutor.addConfigsFiles();
                                             Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],Integer.parseInt(args[6]),proxy,null,null,null);
                                         Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                     }catch (Exception e){
                                         e.printStackTrace();
                                         Console.print(Colors.ANSI_RED()+"An error occurred, probably because you wrote the port down wrong", Level.SEVERE);
                                     }

                                 }else {
                                     try {
                                         JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
                                         if(jvmExecutor == null){
                                             jvmExecutor = new JVMExecutor(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],0,proxy,true);
                                             jvmExecutor.addConfigsFiles();
                                             Console.print(Colors.ANSI_GREEN()+"You have successfully configured the server!");
                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1],args[2], JVMExecutor.Mods.DYNAMIC,args[4],args[5],0,proxy,null,null,null);

                                         Console.print(Colors.ANSI_GREEN()+"You have successfully re-configured the server!");
                                     }catch (Exception e){
                                         e.printStackTrace();
                                         Console.print(Colors.ANSI_RED()+"An error occurred while updating the file.", Level.SEVERE);
                                     }
                                 }
                             }else {
                                 Console.print(Colors.ANSI_RED()+"[!] service add server [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);
                                 Console.print(Colors.ANSI_RED()+"[!] service add proxy [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);
                             }
                         }


                     }
                 }else {
                     Console.print(Colors.ANSI_RED()+"[!] service add server [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);
                     Console.print(Colors.ANSI_RED()+"[!] service add proxy [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);
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