package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.installer.Installer;
import be.alexandre01.dreamnetwork.client.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;
import org.jline.reader.impl.completer.NullCompleter;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Install extends SubCommandCompletor implements SubCommandExecutor {
    public Install(){
        for(InstallationLinks s : InstallationLinks.values()){
            setCompletion(node("service",
                    node("install",
                            node("server", "proxy",
                                    node(NullCompleter.INSTANCE,
                                            node(s.getVer()))))));
        }
    }
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
            InstallationLinks installationLinks;
            try {
                installationLinks = InstallationLinks.getInstallationLinks(args[3]);
            }catch (Exception e){
                System.out.println("[!] The version is incorrect...");
                return true;
            }


            Installer.launchDependInstall(args[3], jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    System.out.println("File Updated with Success");
                    String javaVersion = "default";
                    for(Integer i : installationLinks.getJavaVersion()){
                        if(Client.getInstance().getJavaIndex().getJVersion().containsKey(i)){
                            javaVersion = Client.getInstance().getJavaIndex().getJVersion().get(i).getName();
                            break;
                        }
                    }

                    jvmExecutor.updateConfigFile(jvmExecutor.getPathName(),
                            jvmExecutor.getName(),
                            jvmExecutor.getType(),
                            jvmExecutor.getXms(),
                            jvmExecutor.getXmx(),
                            jvmExecutor.getPort(),
                            jvmExecutor.isProxy(),
                            installationLinks.name().toLowerCase(),
                            jvmExecutor.getStartup(),
                            javaVersion
                            );

                    jvmExecutor.setExec(installationLinks.name().toLowerCase()+".jar");
                    System.gc();
                }
            });
            return true;
        }



        return false;
    }
}
