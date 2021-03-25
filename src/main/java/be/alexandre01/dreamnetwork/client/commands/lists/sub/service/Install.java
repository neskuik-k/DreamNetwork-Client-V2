package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.installer.Installer;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;
import org.slf4j.impl.StaticLoggerBinder;

public class Install implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("install")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service install server [name] [SPIGOT-VER]").red());
                System.out.println(Chalk.on("[!] service install proxy [name] [BUNGEECORD/FLAMECORD/WATERFALL]").red());
                return true;
            }
            System.out.println(args[2]);
            JVMContainer.JVMType jvmType;
            try {
                jvmType = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type is incorrect... try PROXY or SERVER"));
                return true;
            }


            JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2],jvmType);
            if(jvmExecutor == null){
                System.out.println(Chalk.on("[!] The service mentionned is not configurated..").red());
                return true;
            }

            Installer.launchDependInstall(args[3], jvmExecutor.getFileRootDir(), new Installer.IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    System.out.println("updatefile");
                    jvmExecutor.updateConfigFile(jvmExecutor.getPathName(),
                            jvmExecutor.getName(),
                            jvmExecutor.getType(),
                            jvmExecutor.getXms(),
                            jvmExecutor.getXmx(),
                            jvmExecutor.getPort(),
                            jvmExecutor.isProxy(),
                            args[3],
                            jvmExecutor.getStartup());
                }
            });
            return true;
        }



        return false;
    }
}
