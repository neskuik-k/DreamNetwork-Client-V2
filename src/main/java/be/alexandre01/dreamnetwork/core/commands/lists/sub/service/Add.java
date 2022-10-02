package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;



import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;
import com.github.tomaslanger.chalk.Chalk;
import org.jline.builtins.Completers;


import java.util.Arrays;
import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.*;

public class Add extends SubCommandCompletor implements SubCommandExecutor {
    public Add(){
        NodeBuilder nodeBuilder = new NodeBuilder(
                create("service",
                    create("add",
                        create("server", "proxy",
                                create(Completers.AnyCompleter.INSTANCE,
                                        create("STATIC","DYNAMIC",
                                                create("1G","2G")))))));





       /* setCompletion(node("service",
                node("add",
                        node("server", "proxy",
                            node(Completers.AnyCompleter.INSTANCE,
                                node("STATIC","DYNAMIC",
                                    node("1G","2G")))))));


        addCompletor("service","add","server","","STATIC","1G","2G");
        addCompletor("service","add","server","","DYNAMIC","1G","2G");
        addCompletor("service","add","proxy","","STATIC","1G","2G");
        addCompletor("service","add","proxy","","DYNAMIC","1G","2G");*/
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

                     if(!(RamArgumentsChecker.check(args[4]) && RamArgumentsChecker.check(args[5]))){
                         System.out.println(Chalk.on("[!] The RAM Argument is incorrect... try for example: 512M or 1G"));
                         return true;
                     }


                         if(args[3].equalsIgnoreCase("STATIC")){
                             Config.createDir("template/"+args[1]+"/"+args[2]);
                             if(args.length == 7){
                                 try {
                                     JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
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
                                     JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
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
                             return true;

                         }else {
                             if(args[3].equalsIgnoreCase("DYNAMIC")) {
                                 Config.createDir("template/" + args[1] + "/" + args[2]);
                                 if (args.length == 7) {
                                     try {
                                         JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(args[2], jvmType);
                                         if (jvmExecutor == null) {
                                             jvmExecutor = new JVMExecutor(args[1], args[2], JVMExecutor.Mods.DYNAMIC, args[4], args[5], Integer.parseInt(args[6]), proxy, true);
                                             jvmExecutor.addConfigsFiles();
                                             Console.print(Colors.ANSI_GREEN() + "You have successfully configured the server!");
                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1], args[2], JVMExecutor.Mods.DYNAMIC, args[4], args[5], Integer.parseInt(args[6]), proxy, null, null, null);
                                         Console.print(Colors.ANSI_GREEN() + "You have successfully configured the server!");
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                         Console.print(Colors.ANSI_RED() + "An error occurred, probably because you wrote the port down wrong", Level.SEVERE);
                                     }

                                 } else {
                                     try {
                                         JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(args[2], jvmType);
                                         if (jvmExecutor == null) {
                                             jvmExecutor = new JVMExecutor(args[1], args[2], JVMExecutor.Mods.DYNAMIC, args[4], args[5], 0, proxy, true);
                                             jvmExecutor.addConfigsFiles();
                                             Console.print(Colors.ANSI_GREEN() + "You have successfully configured the server!");

                                             return true;
                                         }
                                         jvmExecutor.addConfigsFiles();
                                         jvmExecutor.updateConfigFile(args[1], args[2], JVMExecutor.Mods.DYNAMIC, args[4], args[5], 0, proxy, null, null, null);

                                         Console.print(Colors.ANSI_GREEN() + "You have successfully re-configured the server!");
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                         Console.print(Colors.ANSI_RED() + "An error occurred while updating the file.", Level.SEVERE);
                                     }
                                     return true;
                                 }
                             }

                         }

                     Console.print(Colors.ANSI_RED()+"[!] service add server [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);
                     Console.print(Colors.ANSI_RED()+"[!] service add proxy [name] [DYNAMIC/STATIC] [XMS] [XMX] => add a server ", Level.INFO);

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