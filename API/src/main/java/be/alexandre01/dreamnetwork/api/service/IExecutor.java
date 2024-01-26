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

public interface IExecutor {


    ExecutorCallbacks getGlobalCallbacks();


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
                Kernel32 kernel = null;
                try {
                     Class.forName("com.sun.jna.platform.win32.Kernel32");
                     kernel = Kernel32.INSTANCE;
                }catch (Exception e){
                    return -1;
                }

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
            System.out.println("Erreur");
            if(ex.getClass().getSimpleName().equals("InaccessibleObjectException")){
                System.out.println(Colors.RED+"Please set up the jvm with the flag --add-opens java.base/java.lang=ALL-UNNAMED");
            }
            result = -1;
        }
        return result;
    }



    public ExecutorCallbacks startService();
    public ExecutorCallbacks startService(ExecutorCallbacks callbacks);
    public ExecutorCallbacks startService(String profile);

    public ExecutorCallbacks startService(IConfig jvmConfig);
    public ExecutorCallbacks startService(String profile, ExecutorCallbacks callbacks);

    public ExecutorCallbacks startService(IConfig jvmConfig, ExecutorCallbacks callbacks);

    public ExecutorCallbacks startServices(int i);

    public ExecutorCallbacks startServices(int i, IConfig jvmConfig);
    public ExecutorCallbacks startServices(int i, String profile);
    public default void removeService(IService service){
        service.getStopsCallbacks().forEach(Runnable::run);
        service.getExecutorCallbacks().ifPresent(executorCallbacks -> {
            if(!service.isConnected()){
                if(executorCallbacks.onFail != null){
                    executorCallbacks.onFail.forEach(ExecutorCallbacks.ICallbackFail::whenFail);
                }
            }else {
                if (executorCallbacks.onStop != null) {
                    executorCallbacks.onStop.forEach(iCallbackStop -> iCallbackStop.whenStop(service));
                }
            }
        });
        DNCoreAPI api = DNCoreAPI.getInstance();
       api.getEventsFactory().callEvent(new CoreServiceStopEvent(api, service));

        if (service.getClient() != null) {
            if (!isProxy()) {
                AServiceClient proxy = api.getClientManager().getProxy();
                if (proxy != null) {
                    proxy.getRequestManager().sendRequest(RequestType.PROXY_UNREGISTER_SERVER, service.getFullIndexedName());
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

    @JsonIgnore public Optional<String> getCustomName();

    @JsonIgnore public String getXmx();

    @JsonIgnore public String getPathName();

    @JsonIgnore public String getJavaVersion();

    @JsonIgnore public int getPort();

    public boolean hasExecutable();

    default void onNewServiceStart(ExecutorCallbacks.ICallbackStart callback){
        getGlobalCallbacks().whenStart(callback);
    }

    default void onServiceStop(ExecutorCallbacks.ICallbackStop callback){
        getGlobalCallbacks().whenStop(callback);
    }

    default void onServiceConnect(ExecutorCallbacks.ICallbackConnect callback){
        getGlobalCallbacks().whenConnect(callback);
    }

    default void onServiceFail(ExecutorCallbacks.ICallbackFail callback){
        getGlobalCallbacks().whenFail(callback);
    }

    default void removeCallback(Object... callbacks){
        for (Object callback : callbacks) {
            if(callback instanceof ExecutorCallbacks.ICallbackStart){
                getGlobalCallbacks().onStart.remove(callback);
            }else if(callback instanceof ExecutorCallbacks.ICallbackStop){
                getGlobalCallbacks().onStop.remove(callback);
            }else if(callback instanceof ExecutorCallbacks.ICallbackConnect){
                getGlobalCallbacks().onConnect.remove(callback);
            }else if(callback instanceof ExecutorCallbacks.ICallbackFail){
                getGlobalCallbacks().onFail.remove(callback);
            }
        }
    }




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
