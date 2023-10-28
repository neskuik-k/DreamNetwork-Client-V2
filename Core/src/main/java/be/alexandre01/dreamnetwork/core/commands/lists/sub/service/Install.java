package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ServersNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.gui.install.InstallTemplateConsole;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import lombok.NonNull;

import java.util.ArrayList;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Install extends SubCommandCompletor implements SubCommandExecutor {
    public Install(Command command){
        super(command);
        ArrayList<String> versions = new ArrayList<>();

        for(InstallationLinks s : InstallationLinks.values()) {
            versions.add(s.getVer());
        }

        NodeBuilder nodeBuilder = new NodeBuilder(create(value,
                create("install",
                        create(new BundlesNode(true,true,true),
                                    create(versions.toArray())))));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {


        if(args[0].equalsIgnoreCase("install")){
            if(args.length < 2){

                InstallTemplateConsole i = new InstallTemplateConsole(null);
                i.buildAndRun("m:installTemplate");
                i.addFinishCatch(() -> {
                    Console.setBlockConsole(false);
                    i.exitConsole();
                    i.clearData();
                });
                i.show();
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


            DNUtils.get().getConfigManager().getInstallerManager().launchDependInstall(args[3], jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
                @Override
                public void start() {

                }

                @Override
                public void complete() {
                    Console.printLang("commands.service.install.fileUpdated");
                    String javaVersion = "default";
                    for (int i = installationLinks.getJavaVersion().length-1; i >= 0; i--) {
                        int verID = installationLinks.getJavaVersion()[i];
                        if(Core.getInstance().getJavaIndex().getJVersion().containsKey(verID)){
                            javaVersion = Core.getInstance().getJavaIndex().getJVersion().get(verID).getName();
                            break;
                        }
                    }
                    jvmExecutor.setInstallInfo(installationLinks.name());
                    jvmExecutor.updateConfigFile(jvmExecutor.getPathName(),
                            jvmExecutor.getName(),
                            jvmExecutor.getType(),
                            jvmExecutor.getXms(),
                            jvmExecutor.getXmx(),
                            jvmExecutor.getPort(),
                            jvmExecutor.isProxy(),
                            installationLinks.name().toLowerCase()+".jar",
                            //jvmExecutor.getStartup(),
                            javaVersion
                            );

                    jvmExecutor.setExecutable(installationLinks.name().toLowerCase()+".jar");
                    System.gc();
                }
            });
            return true;
        }



        return false;
    }
}
