package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ServersNode;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.installer.Installer;
import be.alexandre01.dreamnetwork.core.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import java.util.ArrayList;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Install extends SubCommandCompletor implements SubCommandExecutor {
    public Install(){


        ArrayList<String> versions = new ArrayList<>();
        for(InstallationLinks s : InstallationLinks.values()) {
            versions.add(s.getVer());
        }

        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("install",
                        create(new BundlesNode(true,true,true),
                                create(new ServersNode(),
                                    create(versions.toArray()))))));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("install")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service install server [" + Console.getConsole("name") + "] [SPIGOT-VER]").red());
                System.out.println(Chalk.on("[!] service install proxy [" + Console.getConsole("name") + "] [BUNGEECORD/FLAMECORD/WATERFALL]").red());
                return true;
            }


            BundleData bundleData = Main.getBundleManager().getBundleData(args[1]);
            System.out.println(bundleData);
            System.out.println(args[2]);
            JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(args[2],bundleData);
            if(jvmExecutor == null){
                Console.printLang("commands.service.install.notConfigured");
                return true;
            }
            InstallationLinks installationLinks;
            try {
                installationLinks = InstallationLinks.getInstallationLinks(args[3]);
            }catch (Exception e){
                Console.printLang("commands.service.install.incorrectVersion");
                return true;
            }


            Installer.launchDependInstall(args[3], jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    Console.printLang("commands.service.install.fileUpdated");
                    String javaVersion = "default";
                    for(Integer i : installationLinks.getJavaVersion()){
                        if(Core.getInstance().getJavaIndex().getJVersion().containsKey(i)){
                            javaVersion = Core.getInstance().getJavaIndex().getJVersion().get(i).getName();
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
