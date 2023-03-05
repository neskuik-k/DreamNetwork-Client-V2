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
                while ((data = reader.readLine( Colors.PURPLE_BOLD_BRIGHT+"do you want to cancel the creation of the service ? (y or n) > "+Colors.RESET)) != null){
                    if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                        killData();
                        future.onResponse();
                        future.finish();
                    }else {
                        Console.debugPrint("Cancelled.");
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
                        errorLine = Colors.RED+"There is no bundle with the path of "+ args[0];
                        Console.debugPrint(errorLine);
                        return;
                    }

                    bundleData = current;
                    if(bundleData.getJvmType() == IContainer.JVMType.SERVER){
                        console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+"serverName"+Colors.GREEN+" ?" +Colors.WHITE+" Type your serverName: "+Colors.RED);
                    }else {
                        console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+"proxyName"+Colors.GREEN+" ?" +Colors.WHITE+" Type your proxyName: "+Colors.RED);
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
                            errorLine = Colors.RED+"There is a bad character on the name";
                            return;
                        }
                    }

                    serverName = args[0];
                    ConsoleReader.sReader.runMacro(opt[2]);
                    console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+"serverType"+Colors.GREEN+" ?" +Colors.WHITE+" Type your type: "+Colors.RED);
                    console.completorNodes.clear();
                    modsNode = new NodeBuilder(NodeBuilder.create("STATIC"),console);
                    modsNode = new NodeBuilder(NodeBuilder.create("DYNAMIC"),console);
                    console.reloadCompletor();
                    return;
                }

                //PART3
                if(CreateTemplateConsole.this.mods == null){
                    if(!ModsArgumentChecker.check(args[0])){
                        errorLine = Colors.RED+"The mods is incorrect";
                        Console.debugPrint(errorLine);
                        return;
                    }

                    CreateTemplateConsole.this.mods = IJVMExecutor.Mods.valueOf(args[0]);
                    ConsoleReader.sReader.runMacro(opt[3]);
                    modsNode = null;
                    errorLine = null;
                    console.completorNodes.clear();
                    console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+" min ram used"+Colors.GREEN+" ?" +Colors.WHITE+" Type your xms: "+Colors.RED);
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
                        errorLine = Colors.RED+"The RAM argument is incorrect";
                        Console.debugPrint(errorLine);
                        return;
                    }
                    if(CreateTemplateConsole.this.xms == null){
                        CreateTemplateConsole.this.xms = args[0];
                        console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+" max ram used"+Colors.GREEN+" ?" +Colors.WHITE+" Type your xmx: "+Colors.RED);
                        ConsoleReader.sReader.runMacro(opt[4]);
                        return;
                    }
                    CreateTemplateConsole.this.xmx = args[0];
                    ramNode = null;
                    console.completorNodes.clear();
                    ConsoleReader.sReader.runMacro(opt[5]);
                    errorLine = "Type 0 to bind automatic port";
                    console.setWriting(Colors.GREEN+"What is the "+Colors.CYAN+" port "+Colors.GREEN+" ?" +Colors.WHITE+" Type your port: "+Colors.RED);
                    return;
                }

                //PART 5
                if(CreateTemplateConsole.this.port == null){

                    if(!NumberArgumentCheck.check(args[0])){
                        errorLine = "Type 0 to bind automatic port | "+ Colors.RED+" Wrong port; choose a number and not "+ args[0];
                        Console.debugPrint(errorLine);
                        return;
                    }
                    CreateTemplateConsole.this.port = Integer.parseInt(args[0]);

                    // BEGIN OF ADDING SERVER
                    BundleInfo bundleInfo = bundleData.getBundleInfo();
                    Console.debugPrint("Adding server "+serverName+" with "+bundleData.getName()+" bundle");

                    IContainer.JVMType jvmType = bundleInfo.getType();

                    boolean proxy = bundleInfo.getType() == IContainer.JVMType.PROXY;

                    jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(serverName, bundleData);
                    if (jvmExecutor == null) {
                        System.out.println("Creating server "+serverName+" with "+bundleInfo.getName()+" bundle");
                        Config.createDir("bundles/"+bundleData.getName()+"/"+serverName);
                        System.out.println("?");
                        jvmExecutor = new JVMExecutor(bundleData.getName(), serverName, CreateTemplateConsole.this.mods,  CreateTemplateConsole.this.xms,  CreateTemplateConsole.this.xmx,  CreateTemplateConsole.this.port, proxy, true,bundleData);
                        jvmExecutor.addConfigsFiles();
                        Console.print(Colors.ANSI_GREEN() + "You have successfully configured the server!");
                    }else {
                        jvmExecutor.addConfigsFiles();
                        jvmExecutor.updateConfigFile(bundleData.getName(), serverName, CreateTemplateConsole.this.mods, CreateTemplateConsole.this.xms,  CreateTemplateConsole.this.xmx,  CreateTemplateConsole.this.port, proxy, null, null, null);
                        Console.print(Colors.ANSI_GREEN() + "You have successfully configured the server!");
                    }

                    //END OF ADDING
                    console.setWriting(Colors.WHITE+"Do you want to install it ? : "+Colors.RED);
                    ConsoleReader.sReader.runMacro("yes");
                }
                //PART 6
                if(!downloadRequest){
                    if(args[0].equalsIgnoreCase("yes")){
                        downloadRequest = true;
                        console.setWriting(Colors.WHITE+"Which version do you want to use ? : Type the version: "+Colors.RED);
                        console.completorNodes.clear();
                        ArrayList<String> versions = new ArrayList<>();
                        for(InstallationLinks s : InstallationLinks.values()) {
                            versions.add(s.getVer());
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
                    InstallationLinks.getInstallationLinks(args[0]);
                    console.isRunning = false;
                    ConsoleReader.sReader.getTerminal().flush();
                    future.onResponse();
                    if(!tryInstall(args[0],jvmExecutor)){
                        errorLine = Colors.RED+"[!] The version is incorrect...";
                        Console.debugPrint(errorLine);
                        return;
                    }
                }catch (Exception e){

                }



            }

            @Override
            public void consoleChange() {
                clear();
                console.setWriting(Colors.GREEN+"What is your "+Colors.CYAN+"bundle"+Colors.GREEN+" ?" +Colors.WHITE+" Type your bundlename: "+Colors.RED);
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
        try {
            installationLinks = InstallationLinks.getInstallationLinks(type);
        }catch (Exception e){
            return false;
        }

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
                Console.debugPrint("Download Complete !");
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
