package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMConfig;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMStartupConfig;
import be.alexandre01.dreamnetwork.client.utils.clients.RamArgumentsChecker;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;
import org.jline.reader.impl.completer.NullCompleter;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Start extends SubCommandCompletor implements SubCommandExecutor {
    public Start(){
        setCompletion(node("service",
                node("start",
                        node("server", "proxy"))));
        addCompletor("service","start","server");
        addCompletor("service","start","proxy");

    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("start")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service start server [name] (TYPE) ((XMS) (XMX)) (port)").red());
                System.out.println(Chalk.on("[!] service start proxy [name] (TYPE) ((XMS) (XMX)) (port)").red());
                return true;
            }
            JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }


            JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2], type);

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



        return false;
    }

    public JVMExecutor.Mods checkMods(String arg){
        JVMExecutor.Mods mods = null;
        try {
            mods = JVMExecutor.Mods.valueOf(arg.toUpperCase());
        }catch (Exception e){
            System.out.println(Chalk.on("[!] The mods choosed is invalid... choose STATIC or DYNAMIC").red());
            return null;
        }
        return mods;
    }



}
