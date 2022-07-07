package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.client.config.Config;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMConfig;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.JVMStartupConfig;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;

public abstract class IJVMExecutor extends JVMStartupConfig {
    public IJVMExecutor(String pathName, String name, Mods type, String xms, String xmx, int port, boolean proxy, boolean updateFile) {
        super(pathName, name, type, xms, xmx, port, proxy, updateFile);
    }
    public IJVMExecutor(String pathName, String name) {
        super(pathName, name,false);
    }
    public static IJVMExecutor initIfPossible(String pathName, String name, boolean updateFile) {
        Mods type = null;
        String xms = null;
        String xmx = null;
        int port = 0;
        boolean proxy = false;

        try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir") + "/template/" + pathName + "/" + name + "/network.yml")) {
                if (line.startsWith("type:")) {
                    type = Mods.valueOf(line.replace("type:", "").replaceAll(" ", ""));
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
        } catch (Exception e) {
            return null;
        }
        return new JVMExecutor(pathName, name, type, xms, xmx, port, proxy, updateFile);
    }

    static void stopServer(String name, String pathName) {
        String finalName = name.split("-")[0];
        /*if(getProcess(name) != null){
            System.out.println("DESTROY");
            getProcess(name).destroy();
        }*/
        if (JVMExecutor.getStartServerList().contains(name)) {
            JVMExecutor.getStartServerList().remove(name);

        }
        if (JVMExecutor.serversPort.containsKey(name)) {
            int port = JVMExecutor.serversPort.get(name);
            JVMExecutor.serversPort.put("cache-" + JVMExecutor.cache, port);
            JVMExecutor.serversPort.remove(name);
        }


        if (Config.contains(Config.getPath(System.getProperty("user.dir") + "/tmp/" + pathName + "/" + finalName + "/" + name))) {
            Config.removeDir(Config.getPath(System.getProperty("user.dir") + "/tmp/" + pathName + "/" + finalName + "/" + name));
        }
    }

    static void startTest(String typeServer, String name) {

        if (typeServer.equalsIgnoreCase("server") || typeServer.equalsIgnoreCase("proxy")) {
            if (Config.contains("template/" + typeServer + "/" + name)) {
                JVMExecutor process = new JVMExecutor(name, typeServer);
                process.startServer();
                //ServerInstance.startServer(args[2],args[1]);
            } else {
                Console.print(Colors.ANSI_RED() + "You need to configurate your server first before doing this command", Level.WARNING);
            }
        } else {
            Console.print(Colors.ANSI_RED() + "start [SERVER OR PROXY] ServerName", Level.WARNING);
        }

    }

    static void startTest(String typeServer, String name, int port) {

        if (typeServer.equalsIgnoreCase("server") || typeServer.equalsIgnoreCase("proxy")) {
            if (Config.contains("template/" + typeServer + "/" + name)) {
                IJVMExecutor process = new JVMExecutor(name, typeServer);
                process.setPort(port);
                process.startServer();
                //ServerInstance.startServer(args[2],args[1]);
            } else {
                Console.print(Colors.ANSI_RED() + "You need to configurate your server first before doing this command", Level.WARNING);
            }
        } else {
            Console.print(Colors.ANSI_RED() + "start [SERVER OR PROXY] ServerName", Level.WARNING);
        }
    }

    //LINUX ONLY
    static long getPidOfProcess(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }

    //INCLUDE JNA
    public static long getProcessID(Process p) {
        long result = -1;
        try {
            //for windows
            if (p.getClass().getName().equals("java.lang.Win32Process") ||
                    p.getClass().getName().equals("java.lang.ProcessImpl")) {
                Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(p);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE hand = new WinNT.HANDLE();
                hand.setPointer(Pointer.createConstant(handl));
                result = kernel.GetProcessId(hand);
                f.setAccessible(false);
            }
            //for unix based operating systems
            else if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                result = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    static java.util.ArrayList<String> getServerList() {
        return JVMExecutor.getServerList();
    }

    static java.util.ArrayList<String> getStartServerList() {
        return JVMExecutor.getStartServerList();
    }

    static java.util.HashMap<String, java.io.BufferedReader> getProcessServersInput() {
        return JVMExecutor.getProcessServersInput();
    }

    static java.util.ArrayList<Integer> getServersPortList() {
        return JVMExecutor.serversPortList;
    }

    static java.util.ArrayList<Integer> getPortsBlackList() {
        return JVMExecutor.portsBlackList;
    }

    static java.util.HashMap<String, Integer> getServersPort() {
        return JVMExecutor.serversPort;
    }

    static java.util.HashMap<Integer, IService> getServicePort() {
        return JVMExecutor.servicePort;
    }

    static Integer getCache() {
        return JVMExecutor.cache;
    }

    static void setServerList(java.util.ArrayList<String> serverList) {
        JVMExecutor.setServerList(serverList);
    }

    static void setStartServerList(java.util.ArrayList<String> startServerList) {
        JVMExecutor.setStartServerList(startServerList);
    }

    static void setProcessServersInput(java.util.HashMap<String, java.io.BufferedReader> processServersInput) {
        JVMExecutor.setProcessServersInput(processServersInput);
    }

    static void setServersPortList(java.util.ArrayList<Integer> serversPortList) {
        JVMExecutor.serversPortList = serversPortList;
    }

    static void setPortsBlackList(java.util.ArrayList<Integer> portsBlackList) {
        JVMExecutor.portsBlackList = portsBlackList;
    }

    static void setServersPort(java.util.HashMap<String, Integer> serversPort) {
        JVMExecutor.serversPort = serversPort;
    }

    static void setServicePort(java.util.HashMap<Integer, IService> servicePort) {
        JVMExecutor.servicePort = servicePort;
    }

    static void setCache(Integer cache) {
        JVMExecutor.cache = cache;
    }




    public abstract void startServer();

    public abstract void startServer(IConfig jvmConfig);

    public abstract void removeService(int i);

    public abstract IService getService(Integer i);

    public abstract Collection<IService> getServices();


    public enum Mods {
        STATIC("/template/"), DYNAMIC("/tmp/");

        private String path;

        Mods(String path) {
            this.path = path;
        }


        public String getPath() {
            return path;
        }

    }
}
