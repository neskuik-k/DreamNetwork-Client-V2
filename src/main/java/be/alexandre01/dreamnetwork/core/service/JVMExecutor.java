package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.events.list.services.CoreServicePreProcessEvent;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceStartEvent;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.config.EstablishedAction;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaVersion;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.core.utils.clients.IdSet;
import be.alexandre01.dreamnetwork.core.utils.sockets.PortUtils;
import be.alexandre01.dreamnetwork.core.utils.timers.DateBuilderTimer;

import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;


@Ignore
public class JVMExecutor extends JVMStartupConfig implements IJVMExecutor {


    @Getter @Setter private static ArrayList<String> serverList = new ArrayList<>();
    @Getter @Setter private static ArrayList<String> startServerList = new ArrayList<>();

    @Getter @Setter private static HashMap<String,BufferedReader> processServersInput = new HashMap<>();
    @Getter @Setter  public static ArrayList<Integer> serversPortList = new ArrayList<>();
    @Getter @Setter  public static ArrayList<Integer> portsBlackList = new ArrayList<>();
    @Getter @Setter  public static HashMap<String,Integer> serversPort = new HashMap<>();
    @Getter @Setter  public static HashMap<Integer, IService> servicePort = new HashMap<>();
    @Getter @Setter  public static Integer cache = 0;
    private ArrayList<IConfig> queue = new ArrayList<>();
    @Ignore public HashMap<Integer,IService> jvmServices = new HashMap<>();
    @Getter public BundleData bundleData;
    private IdSet idSet = new IdSet();


    public IService staticService = null;


    public JVMExecutor(String pathName,String name, Mods type, String xms, String xmx, int port, boolean proxy,boolean updateFile,BundleData bundleData) {
        super(pathName,name,type,xms,xmx,port,proxy,updateFile);
        this.bundleData = bundleData;

        this.proxy = bundleData.getJvmType() == JVMContainer.JVMType.PROXY;
        JVMContainer.JVMType jvmType = bundleData.getJvmType();
        Core.getInstance().getJvmContainer().addExecutor(this,bundleData);
       // System.out.println("JVMExecutor "+name+" "+type+" "+xms+" "+xmx+" "+port+" "+proxy+" "+updateFile+" "+bundleData);
    }

    public JVMExecutor(String pathName,String name,BundleData bundleData){
        super(pathName,name,false);
        this.bundleData = bundleData;
        this.proxy = bundleData.getJvmType() == JVMContainer.JVMType.PROXY;
        JVMContainer.JVMType jvmType = bundleData.getJvmType();
        Core.getInstance().getJvmContainer().addExecutor(this,bundleData);
    }



    @Override
    public void setPort(int port){
        this.port = port;
    }

    @Override
    public synchronized void startServer(){
        startServer(this);
    }
    @Override
    public synchronized void startServer(IConfig jvmConfig){
        boolean b = queue.isEmpty();
        queue.add(jvmConfig);

        if(!b){
            return;
        }

        startJVM(jvmConfig);
    }

    private synchronized void startJVM(IConfig jvmConfig){
        Console.printLang("service.executor.start", Level.FINE, jvmConfig.getName());
        if(!start(jvmConfig)){
            Console.printLang("service.executor.couldNotStart", Level.WARNING);
            queue.remove(jvmConfig);

            if(!queue.isEmpty()){
                startJVM(queue.get(0));
            }
        }
    }
    private synchronized boolean start(IConfig jvmConfig){

        if(!queue.isEmpty())
        if(!isConfig()) return false;


        if(jvmConfig.getType() == Mods.STATIC && staticService != null){
            Console.printLang("service.executor.alreadyRunning",Level.WARNING);
            return false;
        }

        if(!this.hasExecutable()){
            Console.printLang("service.executor.missingExecutable", this.getExecutable());
            return false;
        }

        if(this.getConfigSize() != getConfigSize() && !isFixedData()){
            saveFile();
        }

        boolean proxy;
        int servers = 1;
        if(jvmConfig.getPathName().contains("server")){
            proxy = false;
        }else {
            proxy = true;
        }

        /*
        Verification proxy allumé
         */
      //  if(Client.getInstance().getProxy() == null && !proxy){
        //    Console.print(Colors.ANSI_RED()+"Veuillez d'abord allumer le Proxy avant d'ouvrir un Serveur.", Level.INFO);
         //   return false;
      //  }


        for (String string : getStartServerList()){
            if(string.startsWith(getFullName()+"-")){

                try{
                    //int num = Integer.parseInt( string.replace(getFullName()+"-",""));
                    servers = idSet.getNextId();
                   // servers ++;
                } catch (NumberFormatException e) {
                    Console.printLang("service.executor.errorOnCreation", Level.WARNING);
                    return false;
                }
            }
        }


        // Console.print(Colors.ANSI_RED+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);
        try {
            String finalname =  jvmConfig.getName()+"-"+servers;
            //System.out.println("finalname "+finalname);
            if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                if(Config.contains("runtimes/"+finalname+"/"+jvmConfig.getName())){
                    Config.removeDir("runtimes/"+jvmConfig.getPathName()+"/"+finalname+"/"+jvmConfig.getName());
                }
                Config.createDir("runtimes/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                int finalServers = servers;
                service.scheduleAtFixedRate(() -> {
                    try {
                        Config.asyncCopy(new File(Config.getPath(new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + getPathName() + "/" + getName())).getAbsolutePath())), new File(Config.getPath("runtimes/" + getPathName() + "/" + getName() + "/" + finalname)), new EstablishedAction() {
                            @Override
                            public void completed() {
                                dateBuilderTimer.loadComplexDate();
                                Console.printLang("service.executor.asyncCopy", Level.FINE, dateBuilderTimer.getLongBuild());
                                isDoneWithSucess.set(true);
                                try {
                                    if(!proceedStarting(finalname, finalServers,jvmConfig)){
                                        queue.remove(jvmConfig);

                                        if(!queue.isEmpty()){
                                            startJVM(queue.get(0));
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void cancelled() {
                                dateBuilderTimer.loadComplexDate();
                                Console.printLang("service.executor.cannotAsyncCopy", dateBuilderTimer.getLongBuild());
                                queue.remove(jvmConfig);

                                if(!queue.isEmpty()){
                                    startJVM(queue.get(0));
                                }
                            }
                        }, this.getExecutable());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    service.shutdown();
                },1,1 ,TimeUnit.SECONDS);

            }else{
                return proceedStarting(finalname,servers,jvmConfig);
            }
            return true;
        } catch (Exception e) {
            Console.printLang("service.executor.couldNotStart", Level.WARNING);
            e.printStackTrace();
            return false;
        }
    }

    private boolean proceedStarting(String finalname,int servers,IConfig jvmConfig) throws IOException {
        Integer port = jvmConfig.getPort();
       // System.out.println("Port: "+port);

        /*if(!this.isProxy() && Client.getInstance().getClientManager().getProxy() == null){
            Console.print(Colors.RED+"You must first turn on the proxy before starting a server.");

            return false;
        }*/
        if(port == 0){

            if(!serversPortList.isEmpty()){
                port = serversPortList.get(serversPortList.size()-1)+2;
                while (portsBlackList.contains(port) || !PortUtils.isAvailable(port,true)){
                    port = port + 2;
                }
                if(!serversPort.isEmpty()){
                    for(Map.Entry<String,Integer> s : serversPort.entrySet()){
                        if(s.getKey().startsWith("cache-")){
                            port = serversPort.get(s.getKey());
                            serversPort.remove(s.getKey(),s.getValue());
                            break;
                        }
                    }
                }

                // System.out.println(port);
                changePort(jvmConfig.getType().getPath()+jvmConfig.getPathName(),finalname,port,bundleData.getJvmType(),jvmConfig.getType());

                port = getCurrentPort(jvmConfig.getType().getPath()+jvmConfig.getPathName(),finalname,bundleData.getJvmType(),jvmConfig.getType());
                if(port == null){
                    Console.printLang("service.executor.notFoundPort", finalname);
                    return false;
                }


                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname,port);
            }else {
                if(jvmConfig.getType().equals(Mods.STATIC)){
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/bundles/"+jvmConfig.getPathName(),finalname,bundleData.getJvmType(),jvmConfig.getType());
                    Console.fine("/bundles/"+jvmConfig.getPathName());
                    if(port == null){
                        Console.printLang("service.executor.notFoundPort", finalname);
                        return false;
                    }
                    serversPortList.add(port);
                    serversPort.put(finalname,port);
                }else{
                    if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                        port = getCurrentPort("/runtimes/"+jvmConfig.getPathName(),finalname,bundleData.getJvmType(),jvmConfig.getType());
                        Console.fine("/runtimes/"+jvmConfig.getPathName());
                        if (port == null) {
                            Console.printLang("service.executor.notFoundPort", finalname);
                            return false;
                        }
                        serversPortList.add(port);
                        serversPort.put(finalname,port);
                    }
                }
            }
        }else {
            if(!serversPortList.contains(port)){
                for(Map.Entry<String,Integer> s : serversPort.entrySet()){
                    if(s.getKey().startsWith("cache-")){

                        port = serversPort.get(s.getKey());
                        serversPort.remove(s.getKey(),s.getValue());
                        break;
                    }
                }

                if(jvmConfig.getType().equals(Mods.STATIC)){
                    changePort("/bundles/"+jvmConfig.getPathName(),finalname,port,bundleData.getJvmType(),jvmConfig.getType());
                }else {
                    if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                        changePort("/runtimes/"+jvmConfig.getPathName(),finalname,port,bundleData.getJvmType(),jvmConfig.getType());
                    }
                }

                portsBlackList.add(port);
                serversPort.put(finalname,port);
            }else {
                Console.printLang("service.executor.portAlreadyUsed", Level.WARNING, port);
                return false;
            }
        }
        String resourcePath = null;
        String startup = null;
        Console.fine(jvmConfig.getJavaVersion());
        Console.fine(Core.getInstance().getJavaIndex().getJMap().keySet());
        if(!Core.getInstance().getJavaIndex().containsKey(jvmConfig.getJavaVersion())){
            Console.print("The java version "+jvmConfig.getJavaVersion()+" is not founded",Level.WARNING);
            return false;
        }

        JavaVersion version = Core.getInstance().getJavaIndex().getJMap().get(jvmConfig.getJavaVersion());
        // check version if valid
        if(Main.getGlobalSettings().isCheckJVMVersionOnServiceStart()){
        try {
            InstallationLinks link = InstallationLinks.valueOf(getInstallInfo());
            boolean b = Arrays.stream(link.getJavaVersion()).anyMatch(v -> v == version.getVersion());
            if(!b){
                Console.print(Colors.RED+"Your Java is incompatible, please install and configure a compatible java on the network.yml file.");
                Console.print(Colors.RED+"List of compatible Java version for "+ getInstallInfo()+": " + Arrays.toString(link.getJavaVersion()));
                Console.print(Colors.RED+"If this error prevention is incorrect and that your java is setup on the good version, you can disable checkJVMVersionOnServiceStart on data/global.yml");
                return false;
            }
        }catch (Exception e){
            Console.print(Colors.RED+"Ignoring version check, can't verify, please check your configuration file",Level.WARNING);
        }
        }
        String javaPath = version.getPath();
        if(jvmConfig.getStartup() != null){
            startup = jvmConfig.getStartup().replaceAll("%java%",javaPath).replaceAll("%xmx%",jvmConfig.getXmx()).replaceAll("%xms%",jvmConfig.getXms());
        }

        Process proc = null;
        Core core = Core.getInstance();
        CoreServicePreProcessEvent preProcessEvent = new CoreServicePreProcessEvent(core.getDnCoreAPI(),jvmConfig);
        core.getEventsFactory().callEvent(preProcessEvent);

        if(preProcessEvent.isCancelled()){
            Console.printLang("service.executor.processCantStartBecauseAddon", Level.WARNING, preProcessEvent.getCancelledBy().getDreamyName());
            return false;
        }


        String customArgs = "";

        if(preProcessEvent.getCustomArguments() != null){
            customArgs += preProcessEvent.getCustomArguments() + " ";
        }
        if(jvmConfig.getType().equals(Mods.DYNAMIC)){
            if(startup != null){
                String jarPath = new File(System.getProperty("user.dir")+ Config.getPath("/bundles/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\","/")+"/"+ this.getExecutable();
                startup = startup.replace("%jar%",customArgs +  jarPath).replace("%exec%",customArgs + jarPath);
                Console.print(startup,Level.FINE);
                proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/runtimes/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname))).redirectErrorStream(true).start();
                //  proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }else {
                String line = javaPath+" -Xms"+jvmConfig.getXms()+" -Xmx"+jvmConfig.getXmx()+ " " + customArgs +"-jar " + new File(System.getProperty("user.dir")+ Config.getPath("/bundles/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath()+"/"+jvmConfig.getExecutable() +" nogui";

                Console.print(line,Level.FINE);
                // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/runtimes/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname))).redirectErrorStream(true).start();
                // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }

        }else {
            if(jvmConfig.getType().equals(Mods.STATIC)){
                if(startup != null){
                    String jarPath = new File(System.getProperty("user.dir")+Config.getPath("/bundles/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\","/")+"/"+jvmConfig.getExecutable();

                    startup = startup.replaceAll("%jar%",customArgs + jarPath).replaceAll("%exec%",customArgs + jarPath);
                    Console.print(startup,Level.FINE);
                    proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/bundles/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()))).redirectErrorStream(true).start();

                    //  proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }else {
                    String line = javaPath + " -Xms"+jvmConfig.getXms()+" -Xmx"+jvmConfig.getXmx()+ customArgs+ " -jar "+  new File(System.getProperty("user.dir")+ Config.getPath("/bundles/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath()+"/"+ this.getExecutable()+" nogui";
                    Console.print(line,Level.FINE);
                    proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/bundles/"+getPathName()+"/"+getName()))).redirectErrorStream(true).start();

                    // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+ exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }


                }
            }

        Console.print("PROCESS ID >" + IJVMExecutor.getProcessID(proc),Level.FINE);
        Console.fine(port);
        JVMService jvmService = JVMService.builder().
                process(proc)
                .jvmExecutor(this)
                .id(servers)
                .port(port)
                .xms(jvmConfig.getXms())
                .xmx(jvmConfig.getXmx())
                .type(jvmConfig.getType())
                .usedConfig(jvmConfig)
                .build();

        jvmServices.put(servers,jvmService);
        servicePort.put(port,jvmService);

       // Thread t = new Thread(JVMReader.builder().jvmService(jvmService).build());
        //t.start();
        Console.printLang("service.executor.serverStartProcess", Level.INFO, getFullName());
        if(jvmConfig.getType() == Mods.DYNAMIC){
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/runtimes/"+getName().toLowerCase()+"/"+getName()+"-"+servers)).getAbsolutePath(), Level.FINE);
        }
        if(jvmConfig.getType() == Mods.STATIC){
            staticService = jvmService;
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/bundles/"+getName().toLowerCase())).getAbsolutePath(), Level.FINE);
        }

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(jvmService.getFullName());
        idSet.add(servers);

        //CONNECTION TO SERVER
        // Connect connect = new Connect("localhost",port+1,"Console","8HetY4474XisrZ2FGwV5z",finalname);
        //   connect.setServer(this);



        core.getEventsFactory().callEvent(new CoreServiceStartEvent(core.getDnCoreAPI(),jvmService));
        //SCREEN SYSTEM
        new Screen(jvmService);

        queue.remove(jvmConfig);
        if(!queue.isEmpty()){
            startJVM(queue.get(0));
        }

        return true;
    }
    @Override
    public void removeService(IService jvmService){
        try{
            int i = jvmService.getId();
            String finalName = getName()+"-"+jvmService.getId();
            if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/runtimes/"+getPathName()+"/"+getName()+"/"+finalName))){
                Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/runtimes/"+getPathName()+"/"+getName()+"/"+finalName));
            }

            if(jvmService.getType() == Mods.STATIC){
                staticService = null;
            }
            jvmServices.remove(i);
            if(servicePort.get(jvmService.getPort()) != null && servicePort.get(jvmService.getPort()) == jvmService){
                serversPortList.remove( Integer.valueOf(jvmService.getPort()));
                servicePort.remove(jvmService.getPort());
            }
            if(serversPort.containsKey(jvmService.getFullName())){

                int port = serversPort.get(jvmService.getFullName());
                serversPort.put("cache-"+cache,port);
                serversPort.remove(jvmService.getFullName());
            }
            getStartServerList().remove(jvmService.getFullName());
            idSet.remove(i);



            jvmServices.remove(i);

            if(!isProxy()){
                be.alexandre01.dreamnetwork.core.connection.core.communication.Client proxy = Core.getInstance().getClientManager().getProxy();
                if(proxy != null){
                    proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_UNREGISTER_SERVER, jvmService.getFullName());
                }
            }
        }catch (Exception e){
            e.printStackTrace(Core.getInstance().formatter.prStr);
        }

    }


    public IService getService(Integer i) {
        return jvmServices.get(i);
    }

    public Collection<IService> getServices() {
        return jvmServices.values();
    }

    @Override
    public IConfig getConfig() {
        return this;
    }

    @Override
    public IStartupConfig getStartupConfig() {
        return this;
    }

    @Override
    public String getFullName() {
        return getBundleData().getName()+"/"+getName();
    }


    public boolean isPortAvailable(int port) {
        Console.printLang("service.executor.checkingPort", port);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(Console.getFromLang("service.executor.invalidStartPort", port));
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }
}

