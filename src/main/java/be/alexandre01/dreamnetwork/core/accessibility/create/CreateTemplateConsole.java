package be.alexandre01.dreamnetwork.core.accessibility.create;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.edit.JVM;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.installer.Installer;
import be.alexandre01.dreamnetwork.core.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import be.alexandre01.dreamnetwork.core.utils.clients.ModsArgumentChecker;
import be.alexandre01.dreamnetwork.core.utils.clients.NumberArgumentCheck;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

public class CreateTemplateConsole {

    protected Console console;
    ScheduledExecutorService executor;
    BundleData bundleData;
    public String serverName;
    public IJVMExecutor.Mods mods;

    public String xms;

    public String xmx;
    public Integer port;

    private JVMExecutor jvmExecutor;

    String errorLine;

    boolean downloadRequest;

    Future future;

    public CreateTemplateConsole(String bundleName, String serverName,String mods, String xms, String xmx, String port){
        console = Console.load("m:create");
        console.setWriting("");



        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public void onKill(LineReader reader) {
                //Shutdown other things
                String data;
                while ((data = reader.readLine(LanguageManager.getMessage("service.creation.cancelCreation"))) != null){
                    if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                        killData();
                        future.onResponse();
                        future.finish();
                    }else {
                        Console.debugPrint(LanguageManager.getMessage("service.creation.cancelCreationCancelled"));
                    }
                    Console.getConsole("m:create").run();
                    break;
                }
            }
        });

        opt = new String[]{bundleName,serverName,mods,xms,xmx,port};
        run();
    }
    NodeBuilder bundleNode;
    NodeBuilder modsNode;
    NodeBuilder ramNode;

    String[] opt;

    public void run(){


        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
               clear();
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                // PART 1
                if(bundleData == null){
                    BundleData current = Core.getInstance().getBundleManager().getBundleData(args[0]);

                    if(current == null){
                        errorLine = LanguageManager.getMessage("bundle.noBundle").replaceFirst("%var%", args[0]);
                        Console.debugPrint(errorLine);
                        return;
                    }

                    bundleData = current;
                    if(bundleData.getJvmType() == IContainer.JVMType.SERVER){
                        console.setWriting(LanguageManager.getMessage("service.creation.ask.serverName"));
                    }else {
                        console.setWriting(LanguageManager.getMessage("service.creation.ask.proxyName"));
                    }
                    errorLine = null;
                    bundleNode = null;
                    ConsoleReader.sReader.runMacro(opt[1]);

                    console.completorNodes.clear();
                    console.reloadCompletor();
                    return;
                }

                //PART 2
                if(serverName == null){
                    String[] illegalChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|","-"};
                    for (String illegalChar : illegalChars) {
                        if(illegalChar.contains(args[0])){
                            errorLine = LanguageManager.getMessage("service.creation.badCharacter");
                            return;
                        }
                    }

                    serverName = args[0];
                    ConsoleReader.sReader.runMacro(opt[2]);
                    console.setWriting(LanguageManager.getMessage("service.creation.ask.serverType"));
                    console.completorNodes.clear();
                    modsNode = new NodeBuilder(NodeBuilder.create("STATIC"),console);
                    modsNode = new NodeBuilder(NodeBuilder.create("DYNAMIC"),console);
                    console.reloadCompletor();
                    return;
                }

                //PART3
                if(CreateTemplateConsole.this.mods == null){
                    if(!ModsArgumentChecker.check(args[0])){
                        errorLine = LanguageManager.getMessage("service.creation.incorrectMods");
                        Console.debugPrint(errorLine);
                        return;
                    }

                    CreateTemplateConsole.this.mods = IJVMExecutor.Mods.valueOf(args[0]);
                    ConsoleReader.sReader.runMacro(opt[3]);
                    modsNode = null;
                    errorLine = null;
                    console.completorNodes.clear();
                    console.setWriting(LanguageManager.getMessage("service.creation.ask.XMS"));
                    ramNode = new NodeBuilder(NodeBuilder.create("512M"),console);
                    ramNode = new NodeBuilder(NodeBuilder.create("1G"),console);
                    ramNode = new NodeBuilder(NodeBuilder.create("2G"),console);
                    ramNode = new NodeBuilder(NodeBuilder.create("4G"),console);
                    console.reloadCompletor();
                    return;
                }
                //PART4
                if(CreateTemplateConsole.this.xms == null || CreateTemplateConsole.this.xmx == null){

                    if(!RamArgumentsChecker.check(args[0])){
                        errorLine = LanguageManager.getMessage("service.creation.incorrectRamArgument");
                        Console.debugPrint(errorLine);
                        return;
                    }
                    if(CreateTemplateConsole.this.xms == null){
                        CreateTemplateConsole.this.xms = args[0];
                        console.setWriting(LanguageManager.getMessage("service.creation.ask.XMX"));
                        ConsoleReader.sReader.runMacro(opt[4]);
                        return;
                    }
                    CreateTemplateConsole.this.xmx = args[0];
                    ramNode = null;
                    console.completorNodes.clear();
                    ConsoleReader.sReader.runMacro(opt[5]);
                    errorLine = LanguageManager.getMessage("service.creation.incorrectPort");
                    console.setWriting(LanguageManager.getMessage("service.creation.ask.port"));
                    return;
                }

                //PART 5
                if(CreateTemplateConsole.this.port == null){

                    if(!NumberArgumentCheck.check(args[0]) && !args[0].equalsIgnoreCase("auto")){
                        errorLine = LanguageManager.getMessage("service.creation.wrongPort").replaceFirst("%var%", args[0]);
                        Console.debugPrint(errorLine);
                        return;
                    }
                    if(args[0].equalsIgnoreCase("auto")){
                        CreateTemplateConsole.this.port = 0;
                    }else {
                        CreateTemplateConsole.this.port = Integer.parseInt(args[0]);
                    }
                    // BEGIN OF ADDING SERVER
                    BundleInfo bundleInfo = bundleData.getBundleInfo();
                    Console.debugPrint(LanguageManager.getMessage("service.creation.addingServerOnBundle").replaceFirst("%var%", serverName).replaceFirst("%var%", bundleData.getName()));

                    IContainer.JVMType jvmType = bundleInfo.getType();

                    boolean proxy = bundleInfo.getType() == IContainer.JVMType.PROXY;

                    jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(serverName, bundleData);
                    if (jvmExecutor == null) {
                        System.out.println(LanguageManager.getMessage("service.creation.creatingServerOnBundle").replaceFirst("%var%", serverName).replaceFirst("%var%", bundleInfo.getName()));
                        Config.createDir("bundles/"+bundleData.getName()+"/"+serverName);
                        jvmExecutor = new JVMExecutor(bundleData.getName(), serverName, CreateTemplateConsole.this.mods,  CreateTemplateConsole.this.xms,  CreateTemplateConsole.this.xmx,  CreateTemplateConsole.this.port, proxy, true,bundleData);
                        jvmExecutor.addConfigsFiles();
                        Console.print(LanguageManager.getMessage("service.creation.serverConfigured"));
                    }else {
                        jvmExecutor.addConfigsFiles();
                        jvmExecutor.updateConfigFile(bundleData.getName(), serverName, CreateTemplateConsole.this.mods, CreateTemplateConsole.this.xms,  CreateTemplateConsole.this.xmx,  CreateTemplateConsole.this.port, proxy, null, null, null);
                        Console.print(LanguageManager.getMessage("service.creation.serverConfigured"));
                    }

                    //END OF ADDING
                    console.setWriting(LanguageManager.getMessage("service.creation.ask.installServer"));
                    ConsoleReader.sReader.runMacro("yes");
                }
                //PART 6
                if(!downloadRequest){
                    if(args[0].equalsIgnoreCase("yes")){
                        downloadRequest = true;
                        console.setWriting(LanguageManager.getMessage("service.creation.install.version"));
                        console.completorNodes.clear();
                        ArrayList<String> versions = new ArrayList<>();
                        for(InstallationLinks s : InstallationLinks.values()) {
                            if(s.getJvmType() == jvmExecutor.bundleData.getJvmType()){
                                versions.add(s.getVer());
                            }
                        }
                        new NodeBuilder(NodeBuilder.create(versions.toArray()),console);
                        console.reloadCompletor();
                        clear();
                        return;
                    }
                    if(args[0].equalsIgnoreCase("no")){
                        console.isRunning = false;
                        killData();
                        future.onResponse();
                        future.finish();
                        return;
                    }
                    return;
                }

                // PART 7

                try {
                    if(!tryInstall(args[0],jvmExecutor)){
                        errorLine = LanguageManager.getMessage("service.creation.install.incorrectVersion");
                        Console.debugPrint(errorLine);
                        return;
                    }
                    console.isRunning = false;
                    ConsoleReader.sReader.getTerminal().flush();
                    future.onResponse();
                }catch (Exception e){

                }



            }

            @Override
            public void consoleChange() {
                clear();
                console.setWriting(LanguageManager.getMessage("service.creation.ask.bundleName"));
                ConsoleReader.sReader.runMacro(opt[0]);
                if(bundleNode == null){
                    bundleNode = new NodeBuilder(NodeBuilder.create(new BundlesNode(false)),console);
                    console.reloadCompletor();
                }
                try {
                    ConsoleReader.sReader.getHistory().purge();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });


    }

    private void killData(){
        errorLine = null;
        port = null;
        xmx = null;
        xms = null;
        mods = null;
        bundleData = null;
        serverName = null;
        jvmExecutor = null;
        this.downloadRequest = false;
    }

    private boolean tryInstall(String type,JVMExecutor jvmExecutor){
        InstallationLinks installationLinks;
        if(type.isEmpty())  return false;

        try {
            installationLinks = InstallationLinks.getInstallationLinks(type);
        }catch (Exception e){
            return false;
        }
        if(installationLinks == null) return false;
        //block console
        Console.setBlockConsole(true);
        String write = console.writing;
        console.setWriting("");
        Installer.launchDependInstall(type, jvmExecutor.getFileRootDir(), new ContentInstaller.IInstall() {
            @Override
            public void start() {
                ConsoleReader.terminal.pause();
            }

            @Override
            public void complete() {
                ConsoleReader.terminal.resume();
                Console.debugPrint(LanguageManager.getMessage("service.creation.install.downloadComplete"));
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
                killData();
                Console.clearConsole();
                console.setWriting(write);
                future.finish();

                Console.setBlockConsole(false);


            }
        });
        return true;
    }

    public void show(String bundleName, String name,String mods, String xms, String xmx, String port,Future future){
        this.future = future;
        opt = new String[]{bundleName,name,mods,xms,xmx,port};
        Console.setActualConsole("m:create",true,false);
    }

    private void clear(){
        Console.clearConsole();
        ASCIIART.sendAdd();
    }
    public interface Future{
        public void onResponse();
        public void finish();
    }
}
