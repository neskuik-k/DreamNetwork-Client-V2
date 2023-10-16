package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServicePreProcessEvent;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceStartEvent;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceStopEvent;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.core.service.deployment.DeployContainer;
import be.alexandre01.dreamnetwork.core.service.deployment.DeployData;
import be.alexandre01.dreamnetwork.core.service.deployment.Deployer;
import be.alexandre01.dreamnetwork.core.service.deployment.VoidDeploy;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaVersion;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.api.utils.clients.IdSet;
import be.alexandre01.dreamnetwork.api.utils.sockets.PortUtils;
import be.alexandre01.dreamnetwork.api.utils.timers.DateBuilderTimer;

import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;

import be.alexandre01.dreamnetwork.utils.Tuple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;


@Ignore @JsonIgnoreProperties(value = {"startupConfig"}, ignoreUnknown = true) @Getter() @Setter
public class JVMExecutor extends JVMStartupConfig implements IJVMExecutor {
    @JsonIgnore @Getter private static ArrayList<String> serverList = new ArrayList<>();
    @JsonIgnore @Getter private static ArrayList<String> startServerList = new ArrayList<>();

    @JsonIgnore  @Getter private static ConcurrentMap<String, BufferedReader> processServersInput = new ConcurrentHashMap<>();
    @JsonIgnore @Getter  public static ArrayList<Integer> serversPortList = new ArrayList<>();

    @JsonIgnore @Getter public static ArrayList<Integer> portsBlackList = new ArrayList<>();
    @JsonIgnore @Getter private static HashMap<Integer, JVMExecutor> portsReserved = new HashMap<>();
    @JsonIgnore @Getter public static HashMap<String, Integer> serversPort = new HashMap<>();
    @JsonIgnore @Getter public static HashMap<Integer, IService> servicePort = new HashMap<>();
    @JsonIgnore @Getter @Setter public static Integer cache = 0;
    @JsonIgnore @Getter private final ArrayList<Tuple<IConfig, ExecutorCallbacks>> queue = new ArrayList<>();
    @Ignore @JsonIgnore
    public HashMap<Integer, IService> jvmServices = new HashMap<>();
    @JsonIgnore public BundleData bundleData;
    @JsonIgnore private IdSet idSet = new IdSet();

    @JsonIgnore private ArrayList<String> charsIds = new ArrayList<>();


    @JsonIgnore public IService staticService = null;

    @JsonIgnore @Getter(AccessLevel.NONE) JVMProfiles jvmProfiles = new JVMProfiles();

    public JVMExecutor(String pathName, String name, Mods type, String xms, String xmx, int port, boolean proxy, boolean updateFile, BundleData bundleData) {
        super(pathName, name, type, xms, xmx, port, proxy, updateFile);
        this.bundleData = bundleData;
        this.proxy = bundleData.getJvmType() == JVMContainer.JVMType.PROXY;
        bundleName = bundleData.getName();
        this.jvmType = bundleData.getJvmType();
        //  System.out.println("JVMExecutor " + name + " " + type + " " + xms + " " + xmx + " " + port + " " + proxy + " " + " " + bundleData);

        //profiles.getProfiles().put("default", JVMConfig.class.cast(this));
        jvmProfiles.loading(new File(getFileRootDir().getAbsolutePath() + "/profiles.yml"));
        Core.getInstance().getJvmContainer().addExecutor(this, bundleData);
        // System.out.println("JVMExecutor "+name+" "+type+" "+xms+" "+xmx+" "+port+" "+proxy+" "+updateFile+" "+bundleData);
    }

    public JVMExecutor(String pathName, String name, BundleData bundleData) {
        super(pathName, name, false);
        this.bundleData = bundleData;
        this.proxy = bundleData.getJvmType() == JVMContainer.JVMType.PROXY;
        this.bundleName = bundleData.getName();
        this.jvmType = bundleData.getJvmType();
        // jvmProfiles.getProfiles().put("default", this);
        jvmProfiles.loading(new File(getFileRootDir().getAbsolutePath() + "/profiles.yml"));
        Core.getInstance().getJvmContainer().addExecutor(this, bundleData);
    }


    @Override
    public void setPort(int port) {
        this.port = port;
        if (port != 0) {
            if (portsReserved.containsKey(port)) {
                //To translate
                Console.print(Colors.RED + "There is a problem with the port allocations of the JVMExecutor -> " + getFullName() + " because there is an another template with the same port", Level.SEVERE);
                return;
            }
            portsReserved.put(port, this);
        }
    }


    @Override @Synchronized
    public ExecutorCallbacks startServer() {
        return startServer(this);
    }

    @Override
    public ExecutorCallbacks startServer(ExecutorCallbacks callbacks) {
        return startServer(this, callbacks);
    }

    @Override
    public ExecutorCallbacks startServer(String profile) {
        return startServer(profile, new ExecutorCallbacks());
    }

    @Override @Synchronized
    public ExecutorCallbacks startServer(IConfig jvmConfig) {
        return startServer(jvmConfig, new ExecutorCallbacks());
    }

    @Override @Synchronized
    public ExecutorCallbacks startServer(String profile, ExecutorCallbacks callbacks) {
        IConfig iConfig = null;
        if (profile != null && getJvmProfiles().isPresent()) {
            IProfiles profiles = getJvmProfiles().get();
            if (profiles.getProfiles().containsKey(profile)) {
                iConfig = profiles.getProfiles().get(profile);
            } else {
                Console.print(Colors.RED + "The profile " + profile + " doesn't exist", Level.SEVERE);
                return null;
            }
            iConfig = JVMStartupConfig.builder(iConfig).buildFrom((IStartupConfig) this);
            return startServer(iConfig, callbacks);
        } else {
            Console.print(Colors.RED + "The profile is null and doesn't exist", Level.SEVERE);
            return null;
        }
    }

    @Override
    public ExecutorCallbacks startServer(IConfig jvmConfig, ExecutorCallbacks c) {
        Console.fine("Checking queing start information");
        boolean b = queue.isEmpty();
        Tuple<IConfig, ExecutorCallbacks> tuple = new Tuple<>(jvmConfig, c);
        queue.add(tuple);
        if (!b) {
            System.out.println("Queue is not empty");
            return c;
        }
        startJVM(tuple);
        return c;
    }

    @Override
    public ExecutorCallbacks startServers(int i) {
        return startServers(i, this);
    }

    @Override
    public ExecutorCallbacks startServers(int i, IConfig jvmConfig) {
        ExecutorCallbacks c = new ExecutorCallbacks();
        for (int j = 0; j < i; j++) {
            startServer(jvmConfig, c);
        }
        return c;
    }

    @Override
    public ExecutorCallbacks startServers(int i, String profile) {
        ExecutorCallbacks c = new ExecutorCallbacks();
        for (int j = 0; j < i; j++) {
            startServer(profile, c);
        }
        return c;
    }

    @Synchronized
    private void startJVM(Tuple<IConfig, ExecutorCallbacks> tuple) {
        IConfig jvmConfig = tuple.a();
        ExecutorCallbacks callbacks = tuple.b();
        Console.printLang("service.executor.start", Level.FINE, jvmConfig.getName());
        if (!start(tuple)) {
            Console.printLang("service.executor.couldNotStart", Level.WARNING);
            queue.remove(tuple);
            if (callbacks != null) {
                callbacks.setHasFailed(true);
                if (callbacks.onFail != null)
                    callbacks.onFail.forEach(ExecutorCallbacks.ICallbackFail::whenFail);
            }
            if (!queue.isEmpty()) {
                //get first insered of linkedhashmap
                Tuple<IConfig, ExecutorCallbacks> renew = queue.get(0);
                startJVM(renew);
            }
        }
    }


    @Synchronized
    private boolean start(Tuple<IConfig, ExecutorCallbacks> tuple) {
        IConfig jvmConfig = tuple.a();
        ExecutorCallbacks callbacks = tuple.b();
        //if (!queue.isEmpty())
        Console.fine("Starting JVMExecutor " + jvmConfig.getName());
        Console.fine("Checking status");
        if (!isConfig()) return false;


        if (jvmConfig.getType() == Mods.STATIC && staticService != null) {
            Console.printLang("service.executor.alreadyRunning", Level.WARNING);
            return false;
        }

        if (!jvmConfig.getDeployers().isEmpty() && jvmConfig.getType() == Mods.STATIC) {
            Console.printLang("service.executor.deployerNotAllowed", Level.WARNING);
            return false;
        }

        if (!this.hasExecutable()) {
            Console.printLang("service.executor.missingExecutable", this.getExecutable());
            return false;
        }

        if (this.getConfigSize() != getConfigSize() && !isFixedData()) {
            saveFile();
        }

        Console.fine("ID creation...");


        int servers;

        /*
        Verification proxy allumé
         */
        //  if(Client.getInstance().getProxy() == null && !proxy){
        //    Console.print(Colors.ANSI_RED()+"Veuillez d'abord allumer le Proxy avant d'ouvrir un Serveur.", Level.INFO);
        //   return false;
        //  }
        servers = idSet.getNextId();
        idSet.add(servers);

        Console.fine("MOD Checking");

        int finalServers = servers;
        String charsID = null;
        // Console.print(Colors.ANSI_RED+new File(System.getProperty("user.dir")+Config.getPath("/template/"+name.toLowerCase()+"/"+name+"-"+servers)).getAbsolutePath(), Level.INFO);
        try {
            Console.fine("Creating server directory");
            String folderName = jvmConfig.getName() + "-" + servers;
            //System.out.println("finalname "+finalname);
            if (jvmConfig.getType().equals(Mods.DYNAMIC)) {
                while (charsIds.contains(charsID = RandomStringUtils.random(6, true, true).toLowerCase())) {
                    // loop until we get a unique id
                }
                folderName += "-" + charsID;
                charsIds.add(charsID);
                Console.fine("Dynamic server mode, creating directory");
                /*if (Config.contains("runtimes/" + jvmConfig.getName() + "/" + folderName)) {
                    Config.removeDir("runtimes/" + jvmConfig.getPathName() + "/" + folderName + "/" + jvmConfig.getName());
                }*/
                Config.createDir("runtimes/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + folderName);
                DateBuilderTimer dateBuilderTimer = new DateBuilderTimer();
                dateBuilderTimer.loadComplexDate();
                AtomicBoolean isDoneWithSucess = new AtomicBoolean(false);
                Console.fine("Deploying via VoidDeploy");
                VoidDeploy voidDeploy = new VoidDeploy(new File(Config.getPath(new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + getPathName() + "/" + getName())).getAbsolutePath())), new DeployData.DeployType[]{DeployData.DeployType.CUSTOM});
                Deployer deployer = new Deployer();
                deployer.addDeploy(voidDeploy);
                if (jvmConfig.getDeployers() != null) {
                    jvmConfig.getDeployers().forEach(s -> {
                        DeployContainer d = Main.getDeployManager().getDeploy(s);
                        if (d != null) {
                            deployer.addDeploy(d.getDeployData());
                        }
                    });
                }

                Console.fine("Deployer is ready");
                String finalName = folderName;
                String finalCharsID = charsID;
                deployer.deploys(new File(System.getProperty("user.dir") + Config.getPath("/runtimes/" + getPathName() + "/" + getName() + "/" + finalName)), new Deployer.DeployAction() {
                    @Override
                    public void completed() {
                        dateBuilderTimer.loadComplexDate();
                        Console.printLang("service.executor.asyncCopy", Level.FINE, dateBuilderTimer.getLongBuild());
                        isDoneWithSucess.set(true);
                        try {
                            if (!proceedStarting(finalName, finalServers, finalCharsID, tuple)) {
                                idSet.remove(finalServers);
                                queue.remove(tuple);
                                if (callbacks != null) {
                                    callbacks.setHasFailed(true);
                                    if (callbacks.onFail != null)
                                        callbacks.onFail.forEach(ExecutorCallbacks.ICallbackFail::whenFail);
                                }
                                if (!queue.isEmpty()) {
                                    Tuple<IConfig, ExecutorCallbacks> renew = queue.get(0);
                                    startJVM(renew);
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
                        queue.remove(tuple);
                        isDoneWithSucess.set(false);

                        if (!queue.isEmpty()) {
                            Tuple<IConfig, ExecutorCallbacks> renew = queue.get(0);
                            startJVM(renew);
                        }
                    }
                }, "/" + this.getExecutable());
            } else {
                return proceedStarting(folderName, servers, charsID, tuple);
            }
            return true;
        } catch (Exception e) {
            Console.printLang("service.executor.couldNotStart", Level.WARNING);
            e.printStackTrace();
            idSet.remove(finalServers);
            return false;
        }
    }
    private int whilePortCheck(int port){
        boolean reserved = false;
        if (portsReserved.containsKey(port)) {
            reserved = true;
            Console.fine("Port reserved on port " + port + " by " + portsReserved.get(port).getFullName());
        }
        int max = Main.getGlobalSettings().getPortRangeInt()[1];
        int min = Main.getGlobalSettings().getPortRangeInt()[0];
        int trying = 0;
        int i = 0;
        while (true) {
            Console.fine(port);
            if (reserved) {
                if (portsReserved.containsKey(port)) {
                    boolean isAccessible = portsReserved.get(port).equals(this);
                    if (!isAccessible || !PortUtils.isAvailable(port, false)) {
                        Console.fine("Is accessible: " + isAccessible + " Is available: " + PortUtils.isAvailable(port, true));
                        port = portCheckIncrement(port,min,max,trying);
                        i++;
                        continue;
                    }
                }

                if (portsBlackList.contains(port)) {
                    port = port + 1;
                    i++;
                    continue;
                }
                break;
            }
            if (!PortUtils.isAvailable(port, false)) {
                Console.fine("Port not available for " + port);
                port = portCheckIncrement(port,min,max,trying);
                i++;
                continue;
            }
            break;
        }
        if(i == 0){
            Console.print("Port " + port + " is available");
        }else {
            Console.print("Port " + port + " is available (after " + i + " tries)");
        }

        return port;
    }

    private int portCheckIncrement(int port,int min,int max,int trying){
            if(port == max){
                if(trying == 1){
                    Console.printLang("service.executor.noPortAvailable", Level.SEVERE);
                    throw new RuntimeException("No port available");
                }
                trying = 1;
                port = min-1;
            }
            port = port + 1;
            return port;
    }

    public Integer findPort(IConfig jvmConfig,String finalname, Integer port){
        if (port == 0) {
            GlobalSettings globalSettings = Main.getGlobalSettings();
            if (!serversPortList.isEmpty() && !globalSettings.isRandomizePort()) {
                port = serversPortList.get(serversPortList.size() - 1) + 1;
                //if containing port, get service and check if he is allowed
                port = whilePortCheck(port);
                if (!serversPort.isEmpty()) {
                    for (Map.Entry<String, Integer> s : serversPort.entrySet()) {
                        if (s.getKey().startsWith("cache-")) {
                            port = serversPort.get(s.getKey());
                            serversPort.remove(s.getKey(), s.getValue());
                            break;
                        }
                    }
                }

                // System.out.println(port);
                int currentPort = getCurrentPort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, bundleData.getJvmType(), jvmConfig.getType());

                changePort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, port, currentPort, bundleData.getJvmType(), jvmConfig.getType());
                //   System.out.println(port);
                serversPortList.add(port);
                serversPort.put(finalname, port);
            } else {
                if (jvmConfig.getType().equals(Mods.STATIC)) {
                    // System.out.println("template/"+pathName);
                    port = getCurrentPort("/bundles/" + jvmConfig.getPathName(), finalname, bundleData.getJvmType(), jvmConfig.getType());
                    Console.fine("/bundles/" + jvmConfig.getPathName());
                    if (port == null) {
                        Console.printLang("service.executor.notFoundPort", finalname);
                        return null;
                    }
                    int min = globalSettings.getPortRangeInt()[0];
                    int max = globalSettings.getPortRangeInt()[1];
                    if(port < min || port > max){
                        port = new Random().nextInt((max - min) + 1) + min;
                    }
                    if(globalSettings.isRandomizePort()){
                        port = new Random().nextInt((max - min) + 1) + min;
                    }
                    port = whilePortCheck(port);
                    serversPortList.add(port);
                    serversPort.put(finalname, port);
                } else {
                    if (jvmConfig.getType().equals(Mods.DYNAMIC)) {
                        port = getCurrentPort("/runtimes/" + jvmConfig.getPathName(), finalname, bundleData.getJvmType(), jvmConfig.getType());
                        Console.fine("/runtimes/" + jvmConfig.getPathName());
                        if (port == null) {
                            Console.printLang("service.executor.notFoundPort", finalname);
                            return null;
                        }
                        if(globalSettings.isRandomizePort()){
                            int min = globalSettings.getPortRangeInt()[0];
                            int max = globalSettings.getPortRangeInt()[1];
                            port = new Random().nextInt((max - min) + 1) + min;
                        }
                        port = whilePortCheck(port);
                        serversPortList.add(port);
                        serversPort.put(finalname, port);
                    }
                }
                int currentPort = getCurrentPort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, bundleData.getJvmType(), jvmConfig.getType());

                changePort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, port,currentPort, bundleData.getJvmType(), jvmConfig.getType());
            }
        } else {
            if (!serversPortList.contains(port)) {
                for (Map.Entry<String, Integer> s : serversPort.entrySet()) {
                    if (s.getKey().startsWith("cache-")) {
                        port = serversPort.get(s.getKey());
                        System.out.println("Changing to cache port: " + s.getKey() + ":" + port);
                        serversPort.remove(s.getKey(), s.getValue());
                        break;
                    }
                }
                int currentPort = getCurrentPort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, bundleData.getJvmType(), jvmConfig.getType());
                changePort(jvmConfig.getType().getPath() + jvmConfig.getPathName(), finalname, port, currentPort, bundleData.getJvmType(), jvmConfig.getType());

                portsBlackList.add(port);
                serversPort.put(finalname, port);
            } else {
                Console.printLang("service.executor.portAlreadyUsed", Level.WARNING, port);
                return null;
            }
        }
        return port;
    }
    private boolean proceedStarting(String finalname, int servers, String charsId, Tuple<IConfig, ExecutorCallbacks> tuple) throws IOException {
        IConfig jvmConfig = tuple.a();
        ExecutorCallbacks callbacks = tuple.b();
        Integer port = jvmConfig.getPort();
        Console.fine("Port on Exec: " + port);

        /*if(!this.isProxy() && Client.getInstance().getClientManager().getProxy() == null){
            Console.print(Colors.RED+"You must first turn on the proxy before starting a server.");

            return false;
        }*/
        port = findPort(jvmConfig,finalname, port);
        if(port == null){
            return false;
        }

        Console.fine("Passing port checkup");
        String resourcePath = null;
        String startup = null;
        Console.fine(jvmConfig.getJavaVersion());
        Console.fine(Core.getInstance().getJavaIndex().getJMap().keySet());
        if (!Core.getInstance().getJavaIndex().containsKey(jvmConfig.getJavaVersion())) {
            Console.print("The java version " + jvmConfig.getJavaVersion() + " is not founded", Level.WARNING);
            return false;
        }

        Console.fine("Searching and checking java version...");

        JavaVersion version = Core.getInstance().getJavaIndex().getJMap().get(jvmConfig.getJavaVersion());
        // check version if valid
        if (Main.getGlobalSettings().isCheckJVMVersionOnServiceStart()) {
            try {
                InstallationLinks link = InstallationLinks.valueOf(getInstallInfo());
                boolean b = Arrays.stream(link.getJavaVersion()).anyMatch(v -> v == version.getVersion());
                if (!b) {
                    Console.print(Colors.RED + "Your Java is incompatible, please install and configure a compatible java on the network.yml file.");
                    Console.print(Colors.RED + "List of compatible Java version for " + getInstallInfo() + ": " + Arrays.toString(link.getJavaVersion()));
                    Console.print(Colors.RED + "If this error prevention is incorrect and that your java is setup on the good version, you can disable checkJVMVersionOnServiceStart on data/global.yml");
                    return false;
                }
            } catch (Exception e) {
                Console.print(Colors.RED + "Ignoring version check, can't verify, please check your configuration file", Level.WARNING);
            }
        }
        String javaPath = version.getPath();
        if (jvmConfig.getStartup() != null) {
            startup = jvmConfig.getStartup().replaceAll("%java%", javaPath).replaceAll("%xmx%", jvmConfig.getXmx()).replaceAll("%xms%", jvmConfig.getXms());
            if (getScreenEnabled() == null) {
                if (!startup.startsWith(javaPath)) {
                    Console.print(Colors.RED + "ScreenViewer disabled for " + jvmConfig.getName() + " because the startup line doesn't start with java path", Level.WARNING);
                    jvmConfig.setScreenEnabled(false);
                }
            }
        } else {
            if (Config.isWindows()) {
                if (getInstallLink().isPresent()) {
                    if (getInstallLink().get().getExecType() == ExecType.BUNGEECORD) {

                        startup = "cmd /c start " + javaPath + " -Xms" + jvmConfig.getXms() + " -Xmx" + jvmConfig.getXmx() + " %args% -jar %exec% nogui";
                        jvmConfig.setScreenEnabled(false);
                        Console.print(Colors.PURPLE_BOLD_BRIGHT + "Changing startup mode, Screen Viewer for BungeeCord is not good supported on Windows\n Switch to terminal console mode", Level.WARNING);
                    }
                }
            }
        }


        Process proc = null;
        Core core = Core.getInstance();
        CoreServicePreProcessEvent preProcessEvent = new CoreServicePreProcessEvent(core.getDnCoreAPI(), jvmConfig);
        core.getEventsFactory().callEvent(preProcessEvent);

        if (preProcessEvent.isCancelled()) {
            Console.printLang("service.executor.processCantStartBecauseAddon", Level.WARNING, preProcessEvent.getCancelledBy().getDreamyName());
            return false;
        }

        String customArgs = "";
        if(ExternalCore.getInstance().isConnected()){
            String connectionId = ExternalCore.getInstance().getConnectionID();
            customArgs += "-DNHost=" + ExternalCore.getInstance().getIp();
            customArgs += " -DNInfo=" + bundleData.getName()+"/"+jvmConfig.getName() + "-" + servers+"+"+connectionId;
        }else{
            customArgs += "-DNHost=" + "this:"+Core.getInstance().getPort();
        }
        if (preProcessEvent.getCustomArguments() != null) {
            customArgs += " " + preProcessEvent.getCustomArguments();
        }
        if (jvmConfig.getType().equals(Mods.DYNAMIC)) {
            if (startup != null) {
                String jarPath = new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + jvmConfig.getPathName() + "/" + jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\", "/") + "/" + this.getExecutable();
                startup = startup.replace("%jar%", jarPath).replace("%exec%", jarPath);
                startup = startup.replace("%args%", customArgs);

                Console.print("JavaLine >" + startup, Level.FINE);
                proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir") + Config.getPath("/runtimes/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + finalname))).start();

                //  proc = Runtime.getRuntime().exec(startup,null ,  new File(System.getProperty("user.dir")+Config.getPath("/temp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            } else {
                String line = javaPath + " -Xms" + jvmConfig.getXms() + " -Xmx" + jvmConfig.getXmx() + " " + customArgs + " -jar " + new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + jvmConfig.getPathName() + "/" + jvmConfig.getName())).getAbsolutePath() + "/" + jvmConfig.getExecutable() + " nogui";

                Console.print("JavaLine > " + line, Level.FINE);
                // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                // ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(new File(System.getProperty("user.dir") + Config.getPath("/logs/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + finalname) + "/logs.txt"));

                proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir") + Config.getPath("/runtimes/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + finalname))).redirectErrorStream(true).start();
                // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/tmp/"+pathName+"/"+name+"/"+finalname)).getAbsoluteFile());
            }

        } else {
            if (jvmConfig.getType().equals(Mods.STATIC)) {
                if (startup != null) {
                    String jarPath = new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + jvmConfig.getPathName() + "/" + jvmConfig.getName())).getAbsolutePath().replaceAll("\\\\", "/") + "/" + jvmConfig.getExecutable();

                    startup = startup.replaceAll("%jar%", jarPath).replaceAll("%exec%", jarPath);
                    startup = startup.replace("%args%", customArgs);
                    Console.print("JavaLine >" + startup, Level.FINE);

                    ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(new File(System.getProperty("user.dir") + Config.getPath("/logs/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + finalname) + "/logs.txt"));

                    proc = new ProcessBuilder(startup.split(" ")).directory(new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + jvmConfig.getPathName() + "/" + jvmConfig.getName()))).redirectErrorStream(true).start();

                    //  proc = Runtime.getRuntime().exec(startup, null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                } else {
                    String line = javaPath + " -Xms" + jvmConfig.getXms() + " -Xmx" + jvmConfig.getXmx() + " " + customArgs + " -jar " + new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + jvmConfig.getPathName() + "/" + jvmConfig.getName())).getAbsolutePath() + "/" + this.getExecutable() + " nogui";
                    Console.print("JavaLine > " + line, Level.FINE);

                    ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(new File(System.getProperty("user.dir") + Config.getPath("/logs/" + jvmConfig.getPathName() + "/" + jvmConfig.getName() + "/" + finalname) + "/logs.txt"));

                    proc = new ProcessBuilder(line.split(" ")).directory(new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + getPathName() + "/" + getName()))).redirectErrorStream(true).start();

                    // proc = Runtime.getRuntime().exec("java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+ Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+ exec+" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                    // proc = Runtime.getRuntime().exec("screen -dmS "+finalname+" java -Duser.language=fr -Djline.terminal=jline.UnsupportedTerminal -Xms"+xms+" -Xmx"+xmx+" -jar " + new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsolutePath()+"/"+exec +" nogui", null ,  new File(System.getProperty("user.dir")+Config.getPath("/template/"+pathName+"/"+name)).getAbsoluteFile());
                }


            }
        }
        long processID = IJVMExecutor.getProcessID(proc);
        Console.fine("PROCESS ID >" + processID);
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
                .executorCallbacks(callbacks)
                .uniqueCharactersID(Optional.ofNullable(charsId))
                .processID(processID)
                .build();


        if (callbacks != null) {
            if (callbacks.onStart != null){
                callbacks.onStart.forEach(iCallbackStart -> iCallbackStart.whenStart(jvmService));
            }

            callbacks.setJvmService(jvmService);
        }
        jvmServices.put(servers, jvmService);
        servicePort.put(port, jvmService);

        // Thread t = new Thread(JVMReader.builder().jvmService(jvmService).build());
        //t.start();
        Console.printLang("service.executor.serverStartProcess", Level.INFO, getFullName());
        if (jvmConfig.getType() == Mods.DYNAMIC) {
            Console.print("Path : " + Colors.ANSI_RESET + new File(System.getProperty("user.dir") + Config.getPath("/runtimes/" + getName().toLowerCase() + "/" + getName() + "-" + servers)).getAbsolutePath(), Level.FINE);
        }
        if (jvmConfig.getType() == Mods.STATIC) {
            staticService = jvmService;
            Console.print("Path : " + Colors.ANSI_RESET + new File(System.getProperty("user.dir") + Config.getPath("/bundles/" + getName().toLowerCase())).getAbsolutePath(), Level.FINE);
        }

        // Main.getInstance().processInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));


        getStartServerList().add(jvmService.getFullName());
        //idSet.add(servers);

        //CONNECTION TO SERVER
        // Connect connect = new Connect("localhost",port+1,"Console","8HetY4474XisrZ2FGwV5z",finalname);
        //   connect.setServer(this);


        core.getEventsFactory().callEvent(new CoreServiceStartEvent(core.getDnCoreAPI(), jvmService));
        //SCREEN SYSTEM

        new Screen(jvmService);
        /*ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {

            executorService.shutdown();
        }, 0, 1, TimeUnit.SECONDS);
*/

        queue.remove(tuple);
        if (!queue.isEmpty()) {
            Tuple<IConfig, ExecutorCallbacks> renew = queue.get(0);
            startJVM(renew);
        }

        return true;
    }

    @Override
    public void removeService(IService jvmService) {
        if(jvmService.isConnected() && jvmService.getProcess().isAlive()){
            System.out.println("Process is alive");
            jvmService.getProcess().destroy();
        }
        if(jvmService.getScreen() != null){
            Console.fine("Stop screen");
            jvmService.getScreen().destroy(true);
        }



        if(jvmService.getClient() != null){
            jvmService.getClient().getChannelHandlerContext().close();
        }else {
            jvmService.getProcess().destroy();
        }

        //   System.out.println("removing service");
        int i = jvmService.getId();
        String dirName = getName() + "-" + jvmService.getId();
        if (jvmService.getUniqueCharactersID().isPresent()) {
            dirName += "-" + jvmService.getUniqueCharactersID().get();
        }
        String finalName = dirName;
        try {
            //Console.debugPrint(Config.getPath(System.getProperty("user.dir") + "/runtimes/" + getPathName() + "/" + getName() + "/" + finalName));
            if (Config.contains(Config.getPath(System.getProperty("user.dir") + "/runtimes/" + getPathName() + "/" + getName() + "/" + finalName))) {
                new Thread() {
                    @Override
                    public void run() {
                        while (Config.removeDir(System.getProperty("user.dir") + "/runtimes/" + getPathName() + "/" + JVMExecutor.this.getName() + "/" + finalName)) {
                            try {
                                Console.fine("Something is blocking " + finalName + " folder, retrying in 1500ms");
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        jvmService.getUniqueCharactersID().ifPresent(string -> charsIds.remove(string));
                    }
                }.start();
            } else {
                jvmService.getUniqueCharactersID().ifPresent(string -> charsIds.remove(string));
            }
            if (jvmService.getType() == Mods.STATIC) {
                staticService = null;
            }

            if (!jvmServices.containsKey(i))
                return;

            if (jvmServices.get(i) != jvmService)
                return;

            jvmServices.remove(i);

            if (servicePort.get(jvmService.getPort()) != null && servicePort.get(jvmService.getPort()) == jvmService) {
                serversPortList.remove(Integer.valueOf(jvmService.getPort()));
                servicePort.remove(jvmService.getPort());
            }
            if (serversPort.containsKey(jvmService.getFullName())) {
                int port = serversPort.get(jvmService.getFullName());
                serversPort.put("cache-" + cache, port);
                serversPort.remove(jvmService.getFullName());
            }
            getStartServerList().remove(jvmService.getFullName());
            idSet.remove(i);

            if(jvmService instanceof JVMService){
                CompletableFuture<Boolean> future = ((JVMService) jvmService).getStopFuture();
                if(future != null){
                    future.complete(true);
                }
            }
            IJVMExecutor.super.removeService(jvmService);
        } catch (Exception e) {
            Console.bug(e);
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
        return getBundleData().getName() + "/" + getName();
    }


    @Override
    public Optional<ExecType> getExecType() {
        return !getInstallLink().isPresent() ? Optional.empty() : Optional.of(getInstallLink().get().getExecType());
    }

    @Override
    public Optional<InstallationLinks> getInstallLink() {
        try {
            return Optional.of(InstallationLinks.valueOf(getInstallInfo()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<IProfiles> getJvmProfiles() {
        return Optional.of(jvmProfiles);
    }
}