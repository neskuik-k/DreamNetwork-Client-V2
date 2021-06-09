package be.alexandre01.dreamnetwork.client.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.config.EstablishedAction;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.utils.timers.DateBuilderTimer;
import jdk.jfr.internal.JVM;
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
        if(!start()){
            Console.print(Colors.ANSI_RED()+"Le serveur n'a pas pu démarré",Level.WARNING);
        }
    }
    private synchronized boolean start(){
        if(!isConfig) return false;

        if(!hasExecutable()){
            Console.print(Colors.ANSI_RED()+"Il manque l'éxécutable du serveur: "+ exec);
            return false;
        }

        if(confSize != getConfigSize() && !isFixedData()){
            update();
        }

        boolean proxy;
        int servers = 0;
        if(pathName.contains("server")){
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

        Console.print("");
        // Console.print(Colors.ANSI_RED+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);
        try {
            String finalname =  name+"-"+servers;

            if(type.equals(Mods.DYNAMIC)){
                if(Config.contains("temp/"+pathName+"/"+finalname+"/"+name)){
                    Config.removeDir("temp/"+pathName+"/"+finalname+"/"+name);
                }
                Config.createDir("temp/"+pathName+"/"+name+"/"+finalname);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                System.out.println("Ok!!!!");
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
                                    proceedStarting(finalname, finalServers);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void cancelled() {
                                dateBuilderTimer.loadComplexDate();
                                System.out.println("Cannot CopiedAsync: actions effectued in "+dateBuilderTimer.getLongBuild());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    service.shutdown();
                },1,1 ,TimeUnit.SECONDS);

            }else{
                return proceedStarting(finalname,servers);
            }
            return true;
        } catch (Exception e) {
            Console.print("Le serveur n'a pas pu démarré",Level.WARNING);
            e.printStackTrace();
            return false;
        }
    }

    private boolean proceedStarting(String finalname,int servers) throws IOException {
        if(port == 0){
            System.out.println("option0");
            if(!serversPortList.isEmpty()){
                System.out.println("option0.5");
                for (Integer string : serversPort.values()){
                    //System.out.println(string);
                }
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
                System.out.println(type.getPath());
                changePort(type.getPath()+pathName,finalname,port,type);

                port = getCurrentPort(type.getPath()+pathName,finalname,type);


                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname,port);
            }else {
                if(type.equals(Mods.STATIC)){
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/template/"+pathName,finalname,type);
                    serversPortList.add(port);
                    serversPort.put(finalname,port);
                }else{
                    if(type.equals(Mods.DYNAMIC)){
                        port = getCurrentPort("/temp/"+pathName,finalname,type);
                        serversPortList.add(port);
                        serversPort.put(finalname,port);
                    }
                }
            }
        }else {
            System.out.println("option2");
            if(!serversPortList.contains(port)){
                System.out.println("option3");
                for(Map.Entry<String,Integer> s : serversPort.entrySet()){
                    if(s.getKey().startsWith("cache-")){

                        port = serversPort.get(s.getKey());
                        serversPort.remove(s.getKey(),s.getValue());
                        break;
                    }
                }

                if(type.equals(Mods.STATIC)){
                    changePort("/template/"+pathName,finalname,port,type);
                }else {
                    if(type.equals(Mods.DYNAMIC)){
                        changePort("/temp/"+pathName,finalname,port,type);
                    }
                }

                portsBlackList.add(port);
                serversPort.put(finalname,port);
            }else {
                Console.print("Le port"+ port +" est déjà utilisé",Level.WARNING);
                return false;
            }
        }
        String resourcePath = null;
        System.out.println("ICI LE CODE EST ACTIVE");
        if(startup != null){
            startup = startup.replaceAll("%exec%",exec).replaceAll("%xmx%",xmx).replaceAll("%xms%",xms);;
            System.out.println(startup);
        }

        Process proc = null;
        if(System.getProperty("os.name").startsWith("Windows")){
            if(type.equals(Mods.DYNAMIC)){
                if(startup != null){
                    exec = new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec;
                    proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());

                }else {
                    proc = Runtime.getRuntime().exec("cmd /c start java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                    // System.out.println(startup);
                    //System.out.println(exec);
                    //startup = startup.replaceAll("%jar%",exec);
                    //SCREEN proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/spigot.jar nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                }

            }else {
                if(type.equals(Mods.STATIC)){
                    if(startup != null){
                        exec = new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec;
                        startup = startup.replaceAll("%jar%",exec);
                        proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    }else {
                        proc = Runtime.getRuntime().exec("cmd /c start java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        //ProcessBuilder pr = new ProcessBuilder("cmd", "/c", "start","java","-Duser.language=fr", "-Djline.terminal=jline.UnsupportedTerminal", "-Xms"+xms,"-Xmx"+xmx,"-jar"+"spigot.jar"+"nogui");
                        //pr.directory( new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        //Console.debugPrint(pr.directory().getAbsolutePath());
                        //  SCREEN proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/spigot.jar nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        //pr.redirectErrorStream(true);
                        //pr.inheritIO();
                        //proc = pr.start();
                        // System.out.println("cmd /c start java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)));
                    }

                }
            }

        }else {
            if(type.equals(Mods.DYNAMIC)){
                if(startup != null){
                    exec = new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec;
                    startup = startup.replaceAll("%jar%",exec);
                    System.out.println(startup);
                    proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                }else {
                    System.out.println("cmd /c start java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec+" nogui");
                    System.out.println( new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                    proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
                }

            }else {
                if(type.equals(Mods.STATIC)){
                    if(startup != null){
                        exec = new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec;
                        startup = startup.replaceAll("%jar%",exec);
                        System.out.println(startup);
                        proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        System.out.println(startup);
                    }else {

                        proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+ exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                        // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    }


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
        Console.print(Colors.ANSI_GREEN()+"Le serveur viens de démarrer le processus",Level.INFO);

        Console.print("Chemins d'accès : "+Colors.ANSI_RESET()+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(finalname);
        Console.debugPrint(String.valueOf(port));

        //CONNECTION TO SERVER
        // Connect connect = new Connect("localhost",port+1,"Console","8HetY4474XisrZ2FGwV5z",finalname);
        //   connect.setServer(this);

        //SCREEN SYSTEM
        new Screen(jvmService);
       

        return true;
    }
    public void removeService(int i){
        JVMService jvmService = jvmServices.get(i);
        String finalName = name+"-"+jvmService.getId();
        System.out.println(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName));
        if(Config.contains(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName))){
            Config.removeDir(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+name+"/"+finalName));
        }
        jvmServices.remove(i);
        servicePort.remove(jvmService.getPort());
        if(serversPort.containsKey(name)){

            int port = serversPort.get(name);
            serversPort.put("cache-"+cache,port);
            System.out.println(serversPort.get("cache-"+cache));
            serversPort.remove(name);
        }
        if(getStartServerList().contains(name)){
            getStartServerList().remove(name);
        }
        jvmServices.remove(i);
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
            System.out.println(serversPort.get("cache-"+cache));
            serversPort.remove(name);
        }


        System.out.println(Config.getPath(System.getProperty("user.dir")+"/temp/"+pathName+"/"+finalName+"/"+name));
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

