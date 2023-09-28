package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.*;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.RamNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.api.utils.clients.RamArgumentsChecker;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Start extends SubCommand {
    public Start(Command command) {
        super(command);
        NodeContainer next = create("STATIC","DYNAMIC",
                create(new RamNode(0,true),create(new RamNode(0,true))));
        NodeContainer profile =  create("profile");
        NodeBuilder nodeBuilder = new NodeBuilder(create(value,
                create("start",
                        create(new BundlesNode(true,true,true),profile,next))));
       // addCompletor("service","start","server");
       // addCompletor("service","start","proxy");
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(!when(sArgs -> {
            String serverPath = sArgs[1];
            IJVMExecutor jvmExecutor = null;
            if(serverPath.contains("/")){
                String[] splitPath = serverPath.split("/");
                String serverName = splitPath[splitPath.length - 1];
                String bundlePath = serverPath.substring(0,(serverPath.length()-serverName.length())-1);
                 jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(serverName, bundlePath);
            }

           /* System.out.println(bundlePath);
            System.out.println(serverName);
            System.out.println("Ah");*/

            if(jvmExecutor == null){

                IJVMExecutor[] array = Core.getInstance().getJvmContainer().getJVMExecutorsFromName(serverPath);
                if(array.length == 1){
                    jvmExecutor = array[0];
                }else {
                    if(array.length == 0){
                        Console.printLang("commands.service.start.notConfigured");
                        fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                        return true;
                    }
                    Console.printLang("commands.service.start.multipleFound");
                    fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                    return true;
                }
            }

            //System.out.println("jvmExecutor = " + jvmExecutor);

            if(sArgs.length < 3){
                jvmExecutor.startServer();
                return true;
            }
            if(args[2].equalsIgnoreCase("profile")){
                if(args.length < 4){
                    Console.printLang("commands.service.start.noProfile");
                    fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                    return true;
                }
                String profile = sArgs[3];

                jvmExecutor.startServer(profile);
                System.out.println("Starting your server with profile " + profile);
                return true;
            }
            JVMExecutor.Mods mods = checkMods(sArgs[2]);
            if(mods == null){
                Console.printLang("commands.service.start.incorrectMods");
                fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                return true;
            }

            if(!(RamArgumentsChecker.check(sArgs[3]) && RamArgumentsChecker.check(sArgs[4]))){
                Console.printLang("commands.service.start.incorrectRAM");
                fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                return true;
            }

            int port = 0;

            if(sArgs.length > 5){
                try {
                    port = Integer.parseInt(sArgs[5]);
                }catch (Exception e){
                    //ignored
                }
            }

            IStartupConfigBuilder builder = IStartupConfig.builder();



            builder.type(mods).xms(sArgs[3]).xmx(sArgs[4]);

            if(port != 0){
                builder.port(port);
            }
            IStartupConfig config = builder.buildFrom(jvmExecutor.getStartupConfig());
            jvmExecutor.startServer(config);
            return true;
        },args,"start","serverPath", "[mods]","[XMS]","[XMX]","[port]")){
            fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
            return true;
        }
        return true;
    }

    public JVMExecutor.Mods checkMods(String arg){
        JVMExecutor.Mods mods = null;
        try {
            mods = JVMExecutor.Mods.valueOf(arg.toUpperCase());
        }catch (Exception e){
            Console.printLang("commands.service.start.invalidChosenMods");
            return null;
        }
        return mods;
    }



}
