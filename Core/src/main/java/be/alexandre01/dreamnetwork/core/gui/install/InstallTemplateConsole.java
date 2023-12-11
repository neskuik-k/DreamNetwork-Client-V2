package be.alexandre01.dreamnetwork.core.gui.install;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;

import java.util.ArrayList;
import java.util.Optional;

public class InstallTemplateConsole extends CoreAccessibilityMenu {
    public InstallTemplateConsole(JVMExecutor jvmExecutor){
        super("m:install");
        if(jvmExecutor == null){
            insertArgumentBuilder("executor", NodeBuilder.create(new BundlesNode(true,true,false)));
            addValueInput(PromptText.create("executor"), new ValueInput() {
                @Override
                public void onTransition(ShowInfos infos) {
                }

                @Override
                public Operation received(PromptText value, String[] args, ShowInfos infos) {
                    Optional<IExecutor> executorOpt = Core.getInstance().getJvmContainer().findExecutor(args[0]);
                    if(!executorOpt.isPresent()){
                        infos.error(Console.getFromLang("service.creation.install.incorrectExecutor"));
                        return errorAndRetry(infos);
                    }
                    IExecutor executor = executorOpt.get();
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
                infos.writing(Console.getFromLang("service.creation.install.tab"));
                ArrayList<String> versions = new ArrayList<>();
                for(InstallationLinks s : InstallationLinks.values()) {
                    if(finalExec.bundleData.getBundleInfo().getExecType() == ExecType.ANY_PROXY){
                        if(s.getExecType().isProxy()){
                            versions.add(s.getVer());
                        }
                        continue;
                    }
                    if(s.getExecType() == finalExec.bundleData.getBundleInfo().getExecType()){
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
                    IConsoleReader.getReader().getTerminal().flush();
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
        if(installationLinks.getExecType() != ExecType.ANY_PROXY){
            if(!jvmExecutor.bundleData.getBundleInfo().getExecType().isProxy()){
                if(installationLinks.getExecType() != jvmExecutor.bundleData.getBundleInfo().getExecType()){
                    return false;
                }
            }
        }

        String write = console.writing;
        console.setWriting("");
        Console.setBlockConsole(true);
        Core.getInstance().getInstallerManager().launchDependInstall(type, jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
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
                        installationLinks.name().toLowerCase()+".jar",
                        javaVersion,
                        jvmExecutor.getCustomName().orElse(null)
                );

                jvmExecutor.setExecutable(installationLinks.name().toLowerCase()+".jar");
                System.gc();
                Console.clearConsole();
                console.setWriting(write);
                jvmExecutor.addConfigsFiles();
                Console.printLang("service.creation.serverConfigured");

               // forceExit();

                Console.setBlockConsole(false);
                injectOperation(Operation.set(Operation.OperationType.FINISH));
                clearData();
            }
        });
        return true;
    }
}
