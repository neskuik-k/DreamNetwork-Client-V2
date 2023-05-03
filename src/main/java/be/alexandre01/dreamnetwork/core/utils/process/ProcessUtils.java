package be.alexandre01.dreamnetwork.core.utils.process;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaVersion;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import sun.management.VMManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;

public class ProcessUtils {

    //INCLUDE JNA
    public static long getID(Process p) {
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

    public static Long getCurrentID() throws Exception {
        // check jvm version
        String version = System.getProperty("java.version");
        if(version.startsWith("1.8")) {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);

            VMManagement management = (VMManagement) jvm.get(runtime);
            Method method = management.getClass().getDeclaredMethod("getProcessId");
            method.setAccessible(true);


            return (Long) method.invoke(management);
        } else {
            Class<?> processHandleClass = Class.forName("java.lang.ProcessHandle");
            processHandleClass.getMethod("current").invoke(null);
            Method pidMethod = processHandleClass.getMethod("pid");
            return (Long) pidMethod.invoke(processHandleClass.getMethod("current").invoke(null));
        }
    }

    public static boolean isStillAllive(long pid) {
        String pidStr = Long.toString(pid);
        String OS = System.getProperty("os.name").toLowerCase();
        String command = null;
        if (OS.indexOf("win") >= 0) {
            Console.fine(Console.getFromLang("core.utils.process.checkAliveWindows", pidStr));
            command = "cmd /c tasklist /FI \"PID eq " + pidStr + "\"";
        } else {
            Console.fine(Console.getFromLang("core.utils.process.checkAliveLinuxUnix", pidStr));
            command = "ps -p " + pidStr;
        }
        return isProcessIdRunning(pidStr, command); // call generic implementation
    }
    private static boolean isProcessIdRunning(String pid, String command) {
        Console.fine(Console.getFromLang("core.utils.process.isRunning.command", command));
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);

            InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            while ((strLine= bReader.readLine()) != null) {
                Console.fine(Console.getFromLang("core.utils.process.isRunning.line", strLine));
                if (strLine.contains(pid + " ")) {
                    Console.fine(Console.getFromLang("core.utils.process.isRunning.running", pid));
                    return true;
                }
            }
            Console.fine(Console.getFromLang("core.utils.process.isRunning.notRunning", pid));

            return false;
        } catch (Exception ex) {
            Console.debugPrint(Console.getFromLang("core.utils.process.isRunning.systemCommandError", command));
            return true;
        }
    }

    public static void killProcess(long process) {
        try {
            // on windows
            if(System.getProperty("os.name").toLowerCase().contains("windows"))
                Runtime.getRuntime().exec("taskkill /F /PID " + process);
            // on linux
            else{
                Runtime.getRuntime().exec("kill -9 " + process);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getDefaultBashJavaVersion(String java) throws Exception {
        String[] commands = {"java", "-version"};
        String ver = null;
        Integer numVer = null;
        Process proc = null;
        try {
            proc = new ProcessBuilder(java,"-version").start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

      /* System.out.println(proc.waitFor());
        if(proc.exitValue() != 0) {
            System.out.println("Error while executing script");
            return ver;
        }*/
            //InputStream stdIn = proc.getInputStream();
       // InputStreamReader isr = new InputStreamReader(stdIn);
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null){
            if(line.contains("\"")){
                String v =  line.split("\"")[1];
                if(v.contains(".")){
                    ver = v.split("\\.")[0];
                    try {
                        numVer = Integer.parseInt(ver);
                    }catch (Exception e){
                        System.out.println("Detected java version is not number");
                        return null;
                    }
                }
            }
            sb.append(line);
        }
        return numVer;
    }
}
