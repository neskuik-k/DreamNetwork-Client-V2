package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.*;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.api.service.IStartupConfigBuilder;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Start extends SubCommand {
    public Start(){
        NodeContainer next = create("STATIC","DYNAMIC",
                create("1G","2G",create("1G","2G")));

        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("start",
                        create(new BundlesNode(true),next))));
        addCompletor("service","start","server");
        addCompletor("service","start","proxy");


    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }


        if(!when(sArgs -> {
            String serverPath = sArgs[1];


            String[] splitPath = serverPath.split("/");

            String serverName = splitPath[splitPath.length - 1];

            String bundlePath = serverPath.substring(0,(serverPath.length()-serverName.length())-1);
           /* System.out.println(bundlePath);
            System.out.println(serverName);
            System.out.println("Ah");*/

            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(serverName, bundlePath);

            //System.out.println("jvmExecutor = " + jvmExecutor);
            if(jvmExecutor == null){
                System.out.println(LanguageManager.getMessage("commands.service.install.notConfigured"));
                fail("service","start", "serverPath", "[mods]" ,"[XMS]" ,"[XMX]", "[port]");
                return true;
            }

            if(sArgs.length < 3){
                jvmExecutor.startServer();
                return true;
            }

            JVMExecutor.Mods mods = checkMods(sArgs[2]);
            if(mods == null){
                System.out.println(LanguageManager.getMessage("commands.service.start.incorrectMods"));
                fail("service","start", "serverPath", LanguageManager.getMessage("mods") ,"[XMS]" ,"[XMX]", "[port]");
                return true;
            }

            if(!(RamArgumentsChecker.check(sArgs[3]) && RamArgumentsChecker.check(sArgs[4]))){
                System.out.println(LanguageManager.getMessage("commands.service.start.incorrectRAM"));
                fail("service","start", "serverPath", LanguageManager.getMessage("mods") ,"[XMS]" ,"[XMX]", "[port]");
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

            jvmExecutor.startServer(builder.build());
            return true;
        },args,"start","serverPath", LanguageManager.getMessage("mods"),"[XMS]","[XMX]","[port]")){
            fail("service","start", "serverPath", LanguageManager.getMessage("mods") ,"[XMS]" ,"[XMX]", "[port]");
            return true;
        }
        /*if(args[0].equalsIgnoreCase("start")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service start bundle/server").red());
                return true;
            }
            JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }


            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(args[2], "main");

            if(jvmExecutor == null){
                System.out.println(Chalk.on("[!] The service mentionned is not configurated..").red());
                return true;
            }


            if(args.length > 3){
                JVMExecutor.Mods mods = checkMods(args[3]);
                if(mods == null){
                    return true;
                }
                if(!(RamArgumentsChecker.check(args[4]) && RamArgumentsChecker.check(args[5]))){
                    System.out.println(Chalk.on("[!] The RAM Argument is incorrect... try for example: 512M or 1G"));
                    return true;
                }
                int port = 0;



                String[] options = new String[args.length - 6];
                for(int i = 6; i < args.length; i++){
                    options[i - 6] = args[i];

                    if(args[i].equalsIgnoreCase("-port")){
                        try {
                            port = Integer.parseInt(args[i + 1]);
                        }catch (Exception e){
                            System.out.println(Chalk.on("[!] The port is incorrect... try for example: 25565").red());
                            return true;
                        }
                    }


                    if(args[i].equalsIgnoreCase("-port")){
                        try {
                            port = Integer.parseInt(args[i + 1]);
                        }catch (Exception e){
                            System.out.println(Chalk.on("[!] The port is incorrect... try for example: 25565").red());
                            return true;
                        }
                    }

                }


                if(args.length > 6){
                   try {
                       port = Integer.parseInt(args[6]);
                   }
                   catch (Exception e){
                       System.out.println(Chalk.on("[!] The port is incorrect... try for example: 25565").red());
                       return true;
                   }
                }



                JVMConfig jvmConfig = JVMStartupConfig.builder()
                        .name(jvmExecutor.getName())
                        .javaVersion(jvmExecutor.getJavaVersion())
                        .exec(jvmExecutor.getExec())
                        .startup(jvmExecutor.getStartup())
                        .type(jvmExecutor.getType())
                        .pathName(jvmExecutor.getPathName())
                        .xms(args[4]).xmx(args[5])
                        .port(port)
                        .type(mods)
                        .build();

                jvmExecutor.startServer(jvmConfig);
            }else {
                jvmExecutor.startServer();
            }
            return true;
        }


*/
        return true;
    }

    public JVMExecutor.Mods checkMods(String arg){
        JVMExecutor.Mods mods = null;
        try {
            mods = JVMExecutor.Mods.valueOf(arg.toUpperCase());
        }catch (Exception e){
            System.out.println(LanguageManager.getMessage("commands.service.start.invalidChosenMods"));
            return null;
        }
        return mods;
    }



}
