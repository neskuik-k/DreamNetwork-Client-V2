package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.events.list.services.CoreServicePreProcessEvent;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceStartEvent;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.config.EstablishedAction;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.core.utils.timers.DateBuilderTimer;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;


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
    public HashMap<Integer,IService> jvmServices = new HashMap<>();

    public IService staticService = null;


    public JVMExecutor(String pathName,String name, Mods type, String xms, String xmx, int port, boolean proxy,boolean updateFile) {
        super(pathName,name,type,xms,xmx,port,proxy,updateFile);
        JVMContainer.JVMType jvmType = ((proxy) ? JVMContainer.JVMType.PROXY : JVMContainer.JVMType.SERVER);
        Core.getInstance().getJvmContainer().addExecutor(this,jvmType);
    }

    public JVMExecutor(String pathName,String name){
        super(pathName,name,false);
        JVMContainer.JVMType jvmType = ((isProxy()) ? JVMContainer.JVMType.PROXY : JVMContainer.JVMType.SERVER);
        Core.getInstance().getJvmContainer().addExecutor(this,jvmType);
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
        Console.print("StartJVM "+ jvmConfig.getName(),Level.FINE);
        if(!start(jvmConfig)){
            Console.print(Colors.ANSI_RED()+"The server could not be started",Level.WARNING);
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
            Console.print(Colors.ANSI_RED()+"The server is already running",Level.WARNING);
            return false;
        }

        if(!this.hasExecutable()){
            Console.print(Colors.ANSI_RED()+"The server executable is missing: "+ getExec());
            return false;
        }

        if(this.getConfigSize() != getConfigSize() && !isFixedData()){
            update();
        }

        boolean proxy;
        int servers = 0;
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
            if(string.startsWith(getName()+"-")){

                try{
                    int num = Integer.parseInt( string.replace(getName()+"-",""));
                    servers++;
                } catch (NumberFormatException e) {
                    Console.print("Une erreur dans la création du serveur",Level.WARNING);
                    return false;
                }
            }
        }


        // Console.print(Colors.ANSI_RED+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);
        try {
            String finalname =  jvmConfig.getName()+"-"+servers;
            if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                if(Config.contains("tmp/"+jvmConfig.getPathName()+"/"+finalname+"/"+jvmConfig.getName())){
                    Config.removeDir("tmp/"+jvmConfig.getPathName()+"/"+finalname+"/"+jvmConfig.getName());
                }
                Config.createDir("tmp/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                int finalServers = servers;
                service.scheduleAtFixedRate(() -> {
                    try {
                        Config.asyncCopy(new File(Config.getPath(new File(System.getProperty("user.dir") + Config.getPath("/template/" + getPathName() + "/" + getName())).getAbsolutePath())), new File(Config.getPath("tmp/" + getPathName() + "/" + getName() + "/" + finalname)), new EstablishedAction() {
                            @Override
                            public void completed() {
                                dateBuilderTimer.loadComplexDate();
                                Console.print("CopiedAsync: actions effectued in "+dateBuilderTimer.getLongBuild(),Level.FINE);
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
                                System.out.println("Cannot CopiedAsync: actions effectued in "+dateBuilderTimer.getLongBuild());
                                queue.remove(jvmConfig);

                                if(!queue.isEmpty()){
                                    startJVM(queue.get(0));
                                }
                            }
                        },getExec());
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
            Console.print("The server could not be started",Level.WARNING);
            e.printStackTrace();
            return false;
        }
    }

    private boolean proceedStarting(String finalname,int servers,IConfig jvmConfig) throws IOException {
        Integer port = jvmConfig.getPort();
        /*if(!this.isProxy() && Client.getInstance().getClientManager().getProxy() == null){
            Console.print(Colors.RED+"You must first turn on the proxy before starting a server.");

            return false;
        }*/
        if(port == 0){

            if(!serversPortList.isEmpty()){
                port = serversPortList.get(serversPortList.size()-1)+2;
                while (portsBlackList.contains(port) || !isPortAvailable(port)){
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
                changePort(jvmConfig.getType().getPath()+jvmConfig.getPathName(),finalname,port,jvmConfig.getType());

                port = getCurrentPort(jvmConfig.getType().getPath()+jvmConfig.getPathName(),finalname,jvmConfig.getType());

                if(port == null){
                    System.out.println(Colors.RED_BOLD+"The port can't be foundable for the server "+ finalname);
                    return false;
                }


                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname,port);
            }else {
                if(jvmConfig.getType().equals(Mods.STATIC)){
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/template/"+jvmConfig.getPathName(),finalname,jvmConfig.getType());
                    serversPortList.add(port);
                    serversPort.put(finalname,port);
                }else{
                    if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                        port = getCurrentPort("/tmp/"+jvmConfig.getPathName(),finalname,jvmConfig.getType());
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
                    changePort("/template/"+jvmConfig.getPathName(),finalname,port,jvmConfig.getType());
                }else {
                    if(jvmConfig.getType().equals(Mods.DYNAMIC)){
                        changePort("/tmp/"+jvmConfig.getPathName(),finalname,port,jvmConfig.getType());
                    }
                }

                portsBlackList.add(port);
                serversPort.put(finalname,port);
            }else {
                Console.print("The port "+ port +" is already used",Level.WARNING);
                return false;
            }
        }
        String resourcePath = null;
        String startup = null;

        String javaPath = Core.getInstance().getJavaIndex().getJMap().get(jvmConfig.getJavaVersion()).getPath();
        if(jvmConfig.getStartup() != null){
            startup = jvmConfig.getStartup().replaceAll("%java%",javaPath).replaceAll("%xmx%",jvmConfig.getXmx()).replaceAll("%xms%",jvmConfig.getXms());
        }

        Process proc = null;
        Core core = Core.getInstance();
        CoreServicePreProcessEvent preProcessEvent = new CoreServicePreProcessEvent(core.getDnCoreAPI(),jvmConfig);
        core.getEventsFactory().callEvent(preProcessEvent);

        if(preProcessEvent.isCancelled()){
            Console.print("The process cannot be started because the "+ preProcessEvent.getCancelledBy().getDreamyName()+ " addon cancelled the event.",Level.WARNING);
            return false;
        }


        String customArgs = "";

        if(preProcessEvent.getCustomArguments() != null){
            customArgs += " " + preProcessEvent.getCustomArguments();
        }
        if(jvmConfig.getType().equals(Mods.DYNAMIC)){
            if(startup != null){
                String jarPath = new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\","/")+"/"+ getExec();
                startup = startup.replace("%jar%",customArgs +  " " + jarPath).replace("%exec%",customArgs +  " " + jarPath);
                Console.print(startup,Level.FINE);
                proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname))).redirectErrorStream(true).start();
                //  proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }else {
                String line = javaPath+" -Xms"+jvmConfig.getXms()+" -Xmx"+jvmConfig.getXmx()+ customArgs +" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath()+"/"+jvmConfig.getExec() +" nogui";

                Console.print(line,Level.FINE);
                // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()+"/"+finalname))).redirectErrorStream(true).start();
                // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }

        }else {
            if(jvmConfig.getType().equals(Mods.STATIC)){
                if(startup != null){
                    String jarPath = new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\","/")+"/"+jvmConfig.getExec();

                    startup = startup.replaceAll("%jar%",customArgs +  " " + jarPath).replaceAll("%exec%",customArgs +  " " + jarPath);
                    Console.print(startup,Level.FINE);
                    proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmConfig.getPathName()+"/"+jvmConfig.getName()))).redirectErrorStream(true).start();

                    //  proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }else {
                    String line = javaPath + " -Xms"+jvmConfig.getXms()+" -Xmx"+jvmConfig.getXmx()+ customArgs+ " -jar "+  new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.getPathName()+"/"+jvmConfig.getName())).getAbsolutePath()+"/"+ getExec()+" nogui";
                    Console.print(line,Level.FINE);
                    proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+getPathName()+"/"+getName()))).redirectErrorStream(true).start();

                    // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+ exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }


                }
            }

        Console.print("PROCESS ID >" + IJVMExecutor.getProcessID(proc),Level.FINE);
        JVMService jvmService = JVMService.builder().
                process(proc)
                .jvmExecutor(this)
                .id(servers)
                .port(port)
                .xms(jvmConfig.getXms())
                .xmx(jvmConfig.getXmx())
                .type(jvmConfig.getType())
                .build();

        jvmServices.put(servers,jvmService);
        servicePort.put(port,jvmService);

       // Thread t = new Thread(JVMReader.builder().jvmService(jvmService).build());
        //t.start();
        Console.print(Colors.GREEN_BOLD+"The server "+Colors.YELLOW_BOLD+finalname+Colors.GREEN_BOLD+" has just started the process",Level.INFO);
        if(jvmConfig.getType() == Mods.DYNAMIC){
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+getName().toLowerCase()+"/"+getName()+"-"+servers)).getAbsolutePath(), Level.FINE);
        }
        if(jvmConfig.getType() == Mods.STATIC){
            staticService = jvmService;
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/template/"+getName().toLowerCase())).getAbsolutePath(), Level.FINE);
        }

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(finalname);

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
            if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/tmp/"+getPathName()+"/"+getName()+"/"+finalName))){
                Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/tmp/"+getPathName()+"/"+getName()+"/"+finalName));
            }

            if(jvmService.getType() == Mods.STATIC){

                //System.out.println("Je supprime le service");
                staticService = null;
            }
            jvmServices.remove(i);
            if(servicePort.get(jvmService.getPort()) != null && servicePort.get(jvmService.getPort()) == jvmService){
                serversPortList.remove( Integer.valueOf(jvmService.getPort()));
                servicePort.remove(jvmService.getPort());
            }
            if(serversPort.containsKey(finalName)){

                int port = serversPort.get(finalName);
                serversPort.put("cache-"+cache,port);
                serversPort.remove(finalName);
            }
            getStartServerList().remove(finalName);




            jvmServices.remove(i);

            if(!isProxy()){
                be.alexandre01.dreamnetwork.core.connection.core.communication.Client proxy = Core.getInstance().getClientManager().getProxy();
                if(proxy != null){
                    proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_UNREGISTER_SERVER, finalName);
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


    public boolean isPortAvailable(int port) {
        System.out.println("Checking if port "+port+" is available...");
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid start port: " + port);
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

