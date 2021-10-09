package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.config.EstablishedAction;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.jvm.JavaVersion;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.utils.timers.DateBuilderTimer;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;


public class JVMExecutor extends JVMStartupConfig{


    @Getter @Setter private static ArrayList<String> serverList = new ArrayList<>();
    @Getter @Setter private static ArrayList<String> startServerList = new ArrayList<>();

    @Getter @Setter private static HashMap<String,BufferedReader> processServersInput = new HashMap<>();
    @Getter @Setter  public static ArrayList<Integer> serversPortList = new ArrayList<>();
    @Getter @Setter  public static ArrayList<Integer> portsBlackList = new ArrayList<>();
    @Getter @Setter   public static HashMap<String,Integer> serversPort = new HashMap<>();
    @Getter @Setter   public static HashMap<Integer, JVMService> servicePort = new HashMap<>();
    @Getter @Setter  public static Integer cache = 0;
    private ArrayList<JVMStartupConfig> queue = new ArrayList<>();
    public HashMap<Integer,JVMService> jvmServices = new HashMap<>();


    public static JVMExecutor initIfPossible(String pathName,String name,boolean updateFile){
        JVMExecutor.Mods type = null;
        String xms = null;
        String xmx = null;
        int port = 0;
        boolean proxy = false;

        try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir") + "/template/" + pathName + "/" + name + "/network.yml")) {
                if (line.startsWith("type:")) {
                    type = JVMExecutor.Mods.valueOf(line.replace("type:", "").replaceAll(" ", ""));
                }
                if (line.startsWith("xms:")) {
                    xms = line.replace("xms:", "").replaceAll(" ", "");
                }
                if (line.startsWith("xmx:")) {
                    xmx = line.replace("xmx:", "").replaceAll(" ", "");
                }
                if (line.startsWith("port:")) {
                    port = Integer.parseInt(line.replace("port:", "").replaceAll(" ", ""));
                }
                if (line.contains("proxy: true")) {
                    proxy = true;
                }
            }
        } catch (Exception e){
            return null;
        }
        return new JVMExecutor(pathName,name,type,xms,xmx,port,proxy,updateFile);
    }

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

    public void setPort(int port){
        this.port = port;
    }

    public synchronized void startServer(){
        startServer(this);
    }
    public synchronized void startServer(JVMStartupConfig jvmStartup){
        boolean b = queue.isEmpty();
        queue.add(jvmStartup);

        if(!b){
            return;
        }

        startJVM(jvmStartup);
    }

    private synchronized void startJVM(JVMStartupConfig jvmStartup){
        System.out.println("StartJVM "+ jvmStartup.name);
        if(!start(jvmStartup)){
            Console.print(Colors.ANSI_RED()+"Le serveur n'a pas pu démarré",Level.WARNING);
            queue.remove(jvmStartup);

            if(!queue.isEmpty()){
                startJVM(queue.get(0));
            }
        }
    }
    private synchronized boolean start(JVMStartupConfig jvmStartup){

        if(!queue.isEmpty())
        if(!isConfig) return false;

        if(!jvmStartup.hasExecutable()){
            Console.print(Colors.ANSI_RED()+"Il manque l'éxécutable du serveur: "+ exec);
            return false;
        }

        if(jvmStartup.confSize != getConfigSize() && !isFixedData()){
            update();
        }

        boolean proxy;
        int servers = 0;
        if(jvmStartup.pathName.contains("server")){
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
            String finalname =  jvmStartup.name+"-"+servers;

            if(type.equals(Mods.DYNAMIC)){
                if(Config.contains("temp/"+jvmStartup.pathName+"/"+finalname+"/"+jvmStartup.name)){
                    Config.removeDir("temp/"+jvmStartup.pathName+"/"+finalname+"/"+jvmStartup.name);
                }
                Config.createDir("temp/"+jvmStartup.pathName+"/"+jvmStartup.name+"/"+finalname);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                int finalServers = servers;
                service.scheduleAtFixedRate(() -> {
                    try {
                        Config.asyncCopy(new File(Config.getPath(new File(System.getProperty("user.dir") + Config.getPath("/template/" + pathName + "/" + name)).getAbsolutePath())), new File(Config.getPath("temp/" + pathName + "/" + name + "/" + finalname)), new EstablishedAction() {
                            @Override
                            public void completed() {
                                dateBuilderTimer.loadComplexDate();
                                System.out.println("CopiedAsync: actions effectued in "+dateBuilderTimer.getLongBuild());
                                isDoneWithSucess.set(true);
                                try {
                                    if(!proceedStarting(finalname, finalServers,jvmStartup)){
                                        queue.remove(jvmStartup);

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
                                queue.remove(jvmStartup);

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
                return proceedStarting(finalname,servers,jvmStartup);
            }
            return true;
        } catch (Exception e) {
            Console.print("Le serveur n'a pas pu démarré",Level.WARNING);
            e.printStackTrace();
            return false;
        }
    }

    private boolean proceedStarting(String finalname,int servers,JVMStartupConfig jvmStartup) throws IOException {
        int port = 0;
        if(!jvmStartup.proxy && Client.getInstance().getClientManager().getProxy() == null){
            Console.print(Colors.RED+"Vous devez d'abord allumer le proxy avant de démarrer un serveur.");

            return false;
        }
        if(jvmStartup.port == 0){

            if(!serversPortList.isEmpty()){
                port = serversPortList.get(serversPortList.size()-1)+2;
                while (portsBlackList.contains(port)){
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
                changePort(type.getPath()+jvmStartup.pathName,finalname,port,type);

                port = getCurrentPort(jvmStartup.type.getPath()+jvmStartup.pathName,finalname,jvmStartup.type);


                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname,port);
            }else {
                if(jvmStartup.type.equals(Mods.STATIC)){
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/template/"+jvmStartup.pathName,finalname,jvmStartup.type);
                    serversPortList.add(port);
                    serversPort.put(finalname,port);
                }else{
                    if(jvmStartup.type.equals(Mods.DYNAMIC)){
                        port = getCurrentPort("/temp/"+jvmStartup.pathName,finalname,jvmStartup.type);
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

                if(jvmStartup.type.equals(Mods.STATIC)){
                    changePort("/template/"+jvmStartup.pathName,finalname,port,jvmStartup.type);
                }else {
                    if(jvmStartup.type.equals(Mods.DYNAMIC)){
                        changePort("/temp/"+jvmStartup.pathName,finalname,port,jvmStartup.type);
                    }
                }

                portsBlackList.add(port);
                serversPort.put(finalname,port);
            }else {
                Console.print("Le port "+ port +" est déjà utilisé",Level.WARNING);
                return false;
            }
        }
        String resourcePath = null;
        String startup = null;

        String javaPath = Client.getInstance().getJavaIndex().getJMap().get(jvmStartup.javaVersion).getPath();
        if(jvmStartup.startup != null){
            startup = jvmStartup.startup.replaceAll("%java%",javaPath).replaceAll("%xmx%",jvmStartup.xmx).replaceAll("%xms%",jvmStartup.xms);
        }

        Process proc = null;


            if(type.equals(Mods.DYNAMIC)){
                if(startup != null){
                    String jarPath = new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmStartup.pathName+"/"+jvmStartup.name)).getAbsolutePath().replaceAll("\\\\","/")+"/"+ exec;
                    startup = startup.replaceAll("%jar%",jarPath).replaceAll("%exec%",jarPath);
                    Console.print(startup,Level.INFO);
                    proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/temp/"+jvmStartup.pathName+"/"+jvmStartup.name+"/"+finalname))).start();
                  //  proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                }else {
                    String line = javaPath+" -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+jvmStartup.xms+" -Xmx"+jvmStartup.xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmStartup.pathName+"/"+jvmStartup.name)).getAbsolutePath()+"/"+jvmStartup.exec +" nogui";

                    Console.print(line,Level.INFO);
                   // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/temp/"+jvmStartup.pathName+"/"+jvmStartup.name+"/"+finalname))).start();
                    // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                }

            }else {
                if(type.equals(Mods.STATIC)){
                    if(startup != null){
                        String jarPath = new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmStartup.pathName+"/"+jvmStartup.name)).getAbsolutePath().replaceAll("\\\\","/")+"/"+jvmStartup.exec;

                        startup = startup.replaceAll("%jar%",jarPath).replaceAll("%exec%",jarPath);
                        System.out.println(startup);
                        proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+jvmStartup.pathName+"/"+jvmStartup.name))).start();
                      //  proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    }else {
                        String line = javaPath + " -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+jvmStartup.xms+" -Xmx"+jvmStartup.xmx+" -jar "+  new File(System.getProperty("user.dir")+ Config.getPath("/template/"+jvmStartup.pathName+"/"+jvmStartup.name)).getAbsolutePath()+"/"+ exec+" nogui";
                        Console.print(line,Level.INFO);
                        proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name))).start();

                        // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+ exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    }


                }
            }


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
        Console.print(Colors.GREEN_BOLD+"Le serveur "+Colors.YELLOW_BOLD+finalname+Colors.GREEN_BOLD+" vient de démarrer le processus",Level.INFO);
        if(type == Mods.DYNAMIC){
            Console.print("Chemins d'accès : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/temp/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.FINE);
        }
        if(type == Mods.STATIC){
            Console.print("Chemins d'accès : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase())).getAbsolutePath(), Level.FINE);
        }

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(finalname);

        //CONNECTION TO SERVER
        // Connect connect = new Connect("localhost",port+1,"Console","8HetY4474XisrZ2FGwV5z",finalname);
        //   connect.setServer(this);

        //SCREEN SYSTEM
        new Screen(jvmService);

        queue.remove(jvmStartup);

        if(!queue.isEmpty()){
            startJVM(queue.get(0));
        }

        return true;
    }
    public void removeService(int i){
        try{
            System.out.println("Removing service...");
            JVMService jvmService = jvmServices.get(i);
            String finalName = name+"-"+jvmService.getId();
            System.out.println(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName));
            if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName))){
                Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName));
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
                ClientManager.Client proxy = Client.getInstance().getClientManager().getProxy();

                proxy.getRequestManager().sendRequest(RequestType.BUNGEECORD_UNREGISTER_SERVER,
                        finalName);
            }
        }catch (Exception e){
            e.printStackTrace(Client.getInstance().formatter.prStr);
        }

    }
    public static void stopServer(String name, String pathName){
        String finalName = name.split("-")[0];
        /*if(getProcess(name) != null){
            System.out.println("DESTROY");
            getProcess(name).destroy();
        }*/
        if(getStartServerList().contains(name)){
            getStartServerList().remove(name);

        }
        if(serversPort.containsKey(name)){
            int port = serversPort.get(name);
            serversPort.put("cache-"+cache,port);
            serversPort.remove(name);
        }


        if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+finalName+"/"+name))){
            Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+finalName+"/"+name));
        }
    }

    public JVMService getService(Integer i) {
        return jvmServices.get(i);
    }
    public Collection<JVMService> getServices() {
        return jvmServices.values();
    }
    public String getName() {
        return name;
    }

    public Mods getType() {
        return type;
    }

    public String getXms() {
        return xms;
    }

    public String getXmx() {
        return xmx;
    }

    public int getPort() {
        return port;
    }

    public static void startTest(String typeServer,String name){

            if(typeServer.equalsIgnoreCase("server")||typeServer.equalsIgnoreCase("proxy")){
                if(Config.contains("template/"+typeServer+"/"+name)){
                    JVMExecutor process = new JVMExecutor(name,typeServer);
                    process.startServer();
                    //ServerInstance.startServer(args[2],args[1]);
                }else {
                    Console.print(Colors.ANSI_RED()+"Veuillez d'abord configurer votre serveur avant de faire cela", Level.WARNING);
                }
            }else {
                Console.print(Colors.ANSI_RED()+"start [SERVER OR PROXY] ServerName",Level.WARNING);
            }

    }
    public static void startTest(String typeServer,String name,int port){

        if(typeServer.equalsIgnoreCase("server")||typeServer.equalsIgnoreCase("proxy")){
            if(Config.contains("template/"+typeServer+"/"+name)){
                JVMExecutor process = new JVMExecutor(name,typeServer);
                process.setPort(port);
                process.startServer();
                //ServerInstance.startServer(args[2],args[1]);
            }else {
                Console.print(Colors.ANSI_RED()+"Veuillez d'abord configurer votre serveur avant de faire cela", Level.WARNING);
            }
        }else {
            Console.print(Colors.ANSI_RED()+"start [SERVER OR PROXY] ServerName",Level.WARNING);
        }

    }

    public enum Mods {
        STATIC("/template/"),DYNAMIC("/temp/");

        private String path;
        Mods(String path){
            this.path = path;
        }


        public String getPath(){
            return path;
        }

    }


}

