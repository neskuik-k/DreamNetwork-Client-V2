package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;

import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;

public interface IJVMExecutor {


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
            if(ex.getClass().getSimpleName().equals("InaccessibleObjectException")){
                System.out.println(Colors.RED+"Please set up the jvm with the flag --add-opens java.base/java.lang=ALL-UNNAMED");
            }
            result = -1;
        }
        return result;
    }

    public static java.util.ArrayList<String> getServerList() {
        return IJVMExecutor.getServerList();
    }

    public static java.util.ArrayList<String> getStartServerList() {
        return IJVMExecutor.getStartServerList();
    }

    public static java.util.HashMap<String, java.io.BufferedReader> getProcessServersInput() {
        return IJVMExecutor.getProcessServersInput();
    }

    public static java.util.ArrayList<Integer> getServersPortList() {
        return IJVMExecutor.getServersPortList();
    }

    public static java.util.ArrayList<Integer> getPortsBlackList() {
        return IJVMExecutor.getPortsBlackList();
    }

    public static java.util.HashMap<String, Integer> getServersPort() {
        return IJVMExecutor.getServersPort();
    }

    public static java.util.HashMap<Integer, IService> getServicePort() {
        return IJVMExecutor.getServicePort();
    }

    public static Integer getCache() {
        return IJVMExecutor.getCache();
    }

    public static void setServerList(java.util.ArrayList<String> serverList) {
        IJVMExecutor.setServerList(serverList);
    }

    public static void setStartServerList(java.util.ArrayList<String> startServerList) {
        IJVMExecutor.setStartServerList(startServerList);
    }

    public static void setProcessServersInput(java.util.HashMap<String, java.io.BufferedReader> processServersInput) {
        IJVMExecutor.setProcessServersInput(processServersInput);
    }





    public static void setCache(Integer cache) {
        IJVMExecutor.setCache(cache);
    }




    public ExecutorCallbacks startServer();
    public ExecutorCallbacks startServer(String profile);

    public ExecutorCallbacks startServer(IConfig jvmConfig);
    public ExecutorCallbacks startServer(String profile,ExecutorCallbacks callbacks);

    public ExecutorCallbacks startServer(IConfig jvmConfig,ExecutorCallbacks callbacks);

    public ExecutorCallbacks startServers(int i);

    public ExecutorCallbacks startServers(int i, IConfig jvmConfig);
    public ExecutorCallbacks startServers(int i, String profile);
    public void removeService(IService service);

    public IService getService(Integer i);

    public Collection<IService> getServices();

    public boolean isProxy();

    public String getName();

    public boolean isConfig();

    public boolean isFixedData();

    public File getFileRootDir();

    public IConfig getConfig();

    public IStartupConfig getStartupConfig();

    public Mods getType();

    public String getXms();

    public String getStartup();

    public String getExecutable();

    public String getXmx();

    public String getPathName();

    public String getJavaVersion();

    public int getPort();

    public boolean hasExecutable();

    public BundleData getBundleData();

    public String getFullName();

    public ExecType getExecType();
    public InstallationLinks getInstallLink();

    public IProfiles getJvmProfiles();



    public enum Mods {
        STATIC("/bundles/"), DYNAMIC("/runtimes/");

        private String path;

        Mods(String path) {
            this.path = path;
        }


        public String getPath() {
            return path;
        }
    }
}
