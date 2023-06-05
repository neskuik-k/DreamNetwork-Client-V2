package be.alexandre01.dreamnetwork.core.console.accessibility.install;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.installer.Installer;
import be.alexandre01.dreamnetwork.core.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;

import java.util.ArrayList;

public class InstallTemplateConsole extends AccessibilityMenu {
    public InstallTemplateConsole(JVMExecutor jvmExecutor){
        super("m:installTemplate");
        if(jvmExecutor == null){
            insertArgumentBuilder("executor", NodeBuilder.create(new BundlesNode(true,true,false)));
            addValueInput(PromptText.create("executor"), new ValueInput() {
                @Override
                public void onTransition(ShowInfos infos) {
                }

                @Override
                public Operation received(PromptText value, String[] args, ShowInfos infos) {
                    IJVMExecutor executor = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(args[0]);
                    if(executor == null){
                        infos.error(Console.getFromLang("service.creation.install.incorrectExecutor"));
                        return errorAndRetry(infos);
                    }
                    if(!(executor instanceof JVMExecutor)){
                        infos.error(Console.getFromLang("service.creation.install.incorrectExecutor"));
                        return errorAndRetry(infos);
                    }
                    return Operation.accepted((JVMExecutor) executor);
                }
            });
        }


        addValueInput(PromptText.create("install"), new ValueInput() {
            JVMExecutor finalExec = jvmExecutor;
            @Override
            public void onTransition(ShowInfos infos) {
                if(finalExec == null){
                    try {
                        finalExec = getOperation("executor").getFrom(JVMExecutor.class);
                        if(finalExec == null){
                            throw new Exception("executor is null");
                        }
                    }catch (Exception e){
                        infos.error(Console.getFromLang("service.creation.install.incorrectExecutor"));
                    }
                }
                infos.writing(Colors.WHITE_BOLD_BRIGHT+ "Press "+Colors.CYAN_BOLD+"TAB"+Colors.WHITE_BOLD_BRIGHT+" to select your version > ");
                ArrayList<String> versions = new ArrayList<>();
                for(InstallationLinks s : InstallationLinks.values()) {
                    if(s.getJvmType() == finalExec.bundleData.getJvmType()){
                        versions.add(s.getVer());
                    }
                }
                insertArgumentBuilder("install",NodeBuilder.create(versions.toArray()));
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(finalExec == null){
                    return errorAndRetry(infos);
                }
                try {

                    if(!tryInstall(args[0],finalExec)){
                        infos.error(Console.getFromLang("service.creation.install.incorrectVersion"));
                        return errorAndRetry(infos);
                    }
                    console.isRunning = false;
                    ConsoleReader.sReader.getTerminal().flush();
                   // future.onResponse();
                }catch (Exception e){
                    return errorAndRetry(infos);
                }
                return Operation.set(Operation.OperationType.WAIT);
            }
        });
    }
    private boolean tryInstall(String type, JVMExecutor jvmExecutor){
        InstallationLinks installationLinks;
        if(type.isEmpty())  return false;

        try {
            installationLinks = InstallationLinks.getInstallationLinks(type);
        }catch (Exception e){
            return false;
        }
        if(installationLinks == null) return false;


        String write = console.writing;
        console.setWriting("");
        Console.setBlockConsole(true);
        Installer.launchDependInstall(type, jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
            @Override
            public void start() {
                //block console
                // Console.setBlockConsole(true);
                //ConsoleReader.terminal.pause();
            }

            @Override
            public void complete() {
                // ConsoleReader.terminal.resume();
                Console.debugPrint(Console.getFromLang("service.creation.install.downloadComplete"));
                String javaVersion = "default";
                for(Integer i : installationLinks.getJavaVersion()){
                    if(Core.getInstance().getJavaIndex().getJVersion().containsKey(i)){
                        javaVersion = Core.getInstance().getJavaIndex().getJVersion().get(i).getName();
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
                        installationLinks.name().toLowerCase(),
                        jvmExecutor.getStartup(),
                        javaVersion
                );

                jvmExecutor.setExecutable(installationLinks.name().toLowerCase()+".jar");
                System.gc();
                Console.clearConsole();
                console.setWriting(write);
                jvmExecutor.addConfigsFiles();
                Console.printLang("service.creation.serverConfigured");

                forceExit();
                injectOperation(Operation.set(Operation.OperationType.FINISH));
            }
        });
        return true;
    }
}
