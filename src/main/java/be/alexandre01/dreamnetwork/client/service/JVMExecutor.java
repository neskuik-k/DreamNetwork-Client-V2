package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.config.EstablishedAction;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.utils.timers.DateBuilderTimer;

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
    @Getter @Setter  public static HashMap<Integer, JVMService> servicePort = new HashMap<>();
    @Getter @Setter  public static Integer cache = 0;
    private ArrayList<JVMConfig> queue = new ArrayList<>();
    public HashMap<Integer,JVMService> jvmServices = new HashMap<>();


    public JVMExecutor(String pathName,String name, Mods type, String xms, String xmx, int port, boolean proxy,boolean updateFile) {
        super(pathName,name,type,xms,xmx,port,proxy,updateFile);
        JVMContainer.JVMType jvmType = ((proxy) ? JVMContainer.JVMType.PROXY : JVMContainer.JVMType.SERVER);
        Client.getInstance().getJvmContainer().addExecutor(this,jvmType);
    }

    public JVMExecutor(String pathName,String name){
        super(pathName,name);
        JVMContainer.JVMType jvmType = ((proxy) ? JVMContainer.JVMType.PROXY : JVMContainer.JVMType.SERVER);
        Client.getInstance().getJvmContainer().addExecutor(this,jvmType);
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
    public synchronized void startServer(JVMConfig jvmConfig){
        boolean b = queue.isEmpty();
        queue.add(jvmConfig);

        if(!b){
            return;
        }

        startJVM(jvmConfig);
    }

    private synchronized void startJVM(JVMConfig jvmConfig){
        Console.print("StartJVM "+ jvmConfig.name,Level.FINE);
        if(!start(jvmConfig)){
            Console.print(Colors.ANSI_RED()+"The server could not be started",Level.WARNING);
            queue.remove(jvmConfig);

            if(!queue.isEmpty()){
                startJVM(queue.get(0));
            }
        }
    }
    private synchronized boolean start(JVMConfig jvmConfig){

        if(!queue.isEmpty())
        if(!isConfig) return false;


        if(jvmConfig.type == Mods.STATIC && !jvmServices.isEmpty()){
            Console.print(Colors.ANSI_RED()+"The server is already running",Level.WARNING);
            return false;
        }

        if(!this.hasExecutable()){
            Console.print(Colors.ANSI_RED()+"The server executable is missing: "+ exec);
            return false;
        }

        if(this.confSize != getConfigSize() && !isFixedData()){
            update();
        }

        boolean proxy;
        int servers = 0;
        if(jvmConfig.pathName.contains("server")){
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
            if(string.startsWith(name+"-")){

                try{
                    int num = Integer.parseInt( string.replace(name+"-",""));
                    servers++;
                } catch (NumberFormatException e) {
                    Console.print("Une erreur dans la création du serveur",Level.WARNING);
                    return false;
                }
            }
        }


        // Console.print(Colors.ANSI_RED+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);
        try {
            String finalname =  jvmConfig.name+"-"+servers;

            if(type.equals(Mods.DYNAMIC)){
                if(Config.contains("tmp/"+jvmConfig.pathName+"/"+finalname+"/"+jvmConfig.name)){
                    Config.removeDir("tmp/"+jvmConfig.pathName+"/"+finalname+"/"+jvmConfig.name);
                }
                Config.createDir("tmp/"+jvmConfig.pathName+"/"+jvmConfig.name+"/"+finalname);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                int finalServers = servers;
                service.scheduleAtFixedRate(() -> {
                    try {
                        Config.asyncCopy(new File(Config.getPath(new File(System.getProperty("user.dir") + Config.getPath("/template/" + pathName + "/" + name)).getAbsolutePath())), new File(Config.getPath("tmp/" + pathName + "/" + name + "/" + finalname)), new EstablishedAction() {
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
                        },exec);
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

    private boolean proceedStarting(String finalname,int servers,JVMConfig jvmConfig) throws IOException {
        Integer port = 0;
        if(!this.proxy && Client.getInstance().getClientManager().getProxy() == null){
            Console.print(Colors.RED+"You must first turn on the proxy before starting a server.");

            return false;
        }
        if(jvmConfig.port == 0){

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
                changePort(type.getPath()+jvmConfig.pathName,finalname,port,type);

                port = getCurrentPort(jvmConfig.type.getPath()+jvmConfig.pathName,finalname,jvmConfig.type);

                if(port == null){
                    System.out.println(Colors.RED_BOLD+"The port can't be foundable for the server "+ finalname);
                    return false;
                }


                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname,port);
            }else {
                if(jvmConfig.type.equals(Mods.STATIC)){
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/template/"+jvmConfig.pathName,finalname,jvmConfig.type);
                    serversPortList.add(port);
                    serversPort.put(finalname,port);
                }else{
                    if(jvmConfig.type.equals(Mods.DYNAMIC)){
                        port = getCurrentPort("/tmp/"+jvmConfig.pathName,finalname,jvmConfig.type);
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

                if(jvmConfig.type.equals(Mods.STATIC)){
                    changePort("/template/"+jvmConfig.pathName,finalname,port,jvmConfig.type);
                }else {
                    if(jvmConfig.type.equals(Mods.DYNAMIC)){
                        changePort("/tmp/"+jvmConfig.pathName,finalname,port,jvmConfig.type);
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

        String javaPath = Client.getInstance().getJavaIndex().getJMap().get(jvmConfig.javaVersion).getPath();
        if(jvmConfig.startup != null){
            startup = jvmConfig.startup.replaceAll("%java%",javaPath).replaceAll("%xmx%",jvmConfig.xmx).replaceAll("%xms%",jvmConfig.xms);
        }

        Process proc = null;


        if(type.equals(Mods.DYNAMIC)){
            if(startup != null){
                String jarPath = new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.pathName+"/"+jvmConfig.name)).getAbsolutePath().replaceAll("\\\\","/")+"/"+ exec;
                startup = startup.replaceAll("%jar%",jarPath).replaceAll("%exec%",jarPath);
                Console.print(startup,Level.FINE);
                proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+jvmConfig.pathName+"/"+jvmConfig.name+"/"+finalname))).redirectErrorStream(true).start();
                //  proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }else {
                String line = javaPath+" -Xms"+jvmConfig.xms+" -Xmx"+jvmConfig.xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.pathName+"/"+jvmConfig.name)).getAbsolutePath()+"/"+jvmConfig.exec +" nogui";

                Console.print(line,Level.FINE);
                // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+jvmConfig.pathName+"/"+jvmConfig.name+"/"+finalname))).redirectErrorStream(true).start();
                // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }

        }else {
            if(type.equals(Mods.STATIC)){
                if(startup != null){
                    String jarPath = new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmConfig.pathName+"/"+jvmConfig.name)).getAbsolutePath().replaceAll("\\\\","/")+"/"+jvmConfig.exec;

                    startup = startup.replaceAll("%jar%",jarPath).replaceAll("%exec%",jarPath);
                    Console.print(startup,Level.FINE);
                    proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmConfig.pathName+"/"+jvmConfig.name))).redirectErrorStream(true).start();

                    //  proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }else {
                    String line = javaPath + " -Xms"+jvmConfig.xms+" -Xmx"+jvmConfig.xmx+" -jar "+  new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmConfig.pathName+"/"+jvmConfig.name)).getAbsolutePath()+"/"+ exec+" nogui";
                    Console.print(line,Level.FINE);
                    proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name))).redirectErrorStream(true).start();

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
                .build();

        jvmServices.put(servers,jvmService);
        servicePort.put(port,jvmService);

       // Thread t = new Thread(JVMReader.builder().jvmService(jvmService).build());
        //t.start();
        Console.print(Colors.GREEN_BOLD+"The server "+Colors.YELLOW_BOLD+finalname+Colors.GREEN_BOLD+" has just started the process",Level.INFO);
        if(type == Mods.DYNAMIC){
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.FINE);
        }
        if(type == Mods.STATIC){
            Console.print("Path : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase())).getAbsolutePath(), Level.FINE);
        }

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(finalname);

        //CONNECTION TO SERVER
        // Connect connect = new Connect("localhost",port+1,"Console","8HetY4474XisrZ2FGwV5z",finalname);
        //   connect.setServer(this);

        //SCREEN SYSTEM
        new Screen(jvmService);

        queue.remove(jvmConfig);

        if(!queue.isEmpty()){
            startJVM(queue.get(0));
        }

        return true;
    }
    @Override
    public void removeService(int i){
        try{
            JVMService jvmService = jvmServices.get(i);
            String finalName = name+"-"+jvmService.getId();
            if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/tmp/"+pathName+"/"+name+"/"+finalName))){
                Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/tmp/"+pathName+"/"+name+"/"+finalName));
            }

            jvmServices.remove(i);
            serversPortList.remove( Integer.valueOf(jvmService.getPort()));
            servicePort.remove(jvmService.getPort());
            if(serversPort.containsKey(finalName)){

                int port = serversPort.get(finalName);
                serversPort.put("cache-"+cache,port);
                serversPort.remove(finalName);
            }
            getStartServerList().remove(finalName);




            jvmServices.remove(i);

            if(!jvmService.getJvmExecutor().isProxy()){
                be.alexandre01.dreamnetwork.client.connection.core.communication.Client proxy = Client.getInstance().getClientManager().getProxy();

                proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_UNREGISTER_SERVER,
                        finalName);
            }
        }catch (Exception e){
            e.printStackTrace(Client.getInstance().formatter.prStr);
        }

    }

    @Override
    public JVMService getService(Integer i) {
        return jvmServices.get(i);
    }
    @Override
    public Collection<JVMService> getServices() {
        return jvmServices.values();
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Mods getType() {
        return type;
    }

    @Override
    public String getXms() {
        return xms;
    }

    @Override
    public String getXmx() {
        return xmx;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
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

