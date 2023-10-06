package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.events.list.services.CoreServiceStopEvent;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;

import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

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



    public ExecutorCallbacks startServer();
    public ExecutorCallbacks startServer(ExecutorCallbacks callbacks);
    public ExecutorCallbacks startServer(String profile);

    public ExecutorCallbacks startServer(IConfig jvmConfig);
    public ExecutorCallbacks startServer(String profile,ExecutorCallbacks callbacks);

    public ExecutorCallbacks startServer(IConfig jvmConfig,ExecutorCallbacks callbacks);

    public ExecutorCallbacks startServers(int i);

    public ExecutorCallbacks startServers(int i, IConfig jvmConfig);
    public ExecutorCallbacks startServers(int i, String profile);
    public default void removeService(IService service){
        service.getExecutorCallbacks().ifPresent(executorCallbacks -> {
            if (executorCallbacks.onStop != null) {
                executorCallbacks.onStop.whenStop(service);
            }
        });
        DNCoreAPI api = DNCoreAPI.getInstance();
       api.getEventsFactory().callEvent(new CoreServiceStopEvent(api, service));

        if (service.getClient() != null) {
            if (!isProxy()) {
                AServiceClient proxy = api.getClientManager().getProxy();
                if (proxy != null) {
                    proxy.getRequestManager().sendRequest(RequestType.PROXY_UNREGISTER_SERVER, service.getFullName());
                }
            }
        }
    }

    public IService getService(Integer i);

    @JsonIgnore public Collection<IService> getServices();

    public boolean isProxy();

    @JsonIgnore public String getName();

    public boolean isConfig();

    public boolean isFixedData();

    @JsonIgnore public File getFileRootDir();

    @JsonIgnore public IConfig getConfig();

    @JsonIgnore public IStartupConfig getStartupConfig();

    @JsonIgnore public Mods getType();

    @JsonIgnore public String getXms();

    @JsonIgnore public String getStartup();

    @JsonIgnore public String getExecutable();

    @JsonIgnore public String getXmx();

    @JsonIgnore public String getPathName();

    @JsonIgnore public String getJavaVersion();

    @JsonIgnore public int getPort();

    public boolean hasExecutable();

    @JsonIgnore public BundleData getBundleData();

    @JsonIgnore public String getFullName();


    @JsonIgnore public Optional<ExecType> getExecType();
    @JsonIgnore public Optional<InstallationLinks> getInstallLink();

    @JsonIgnore public Optional<IProfiles> getJvmProfiles();



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
