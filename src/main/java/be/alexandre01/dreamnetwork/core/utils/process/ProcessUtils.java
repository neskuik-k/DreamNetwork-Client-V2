package be.alexandre01.dreamnetwork.core.utils.process;

import be.alexandre01.dreamnetwork.core.console.Console;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import sun.management.VMManagement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
            Console.fine("Check alive Windows mode. Pid: ["+pidStr+"]");
            command = "cmd /c tasklist /FI \"PID eq " + pidStr + "\"";
        } else {
            Console.fine("Check alive Linux/Unix mode. Pid: ["+pidStr+"]");
            command = "ps -p " + pidStr;
        }
        return isProcessIdRunning(pidStr, command); // call generic implementation
    }
    private static boolean isProcessIdRunning(String pid, String command) {
        Console.fine("Command ["+command+"]");
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);

            InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            while ((strLine= bReader.readLine()) != null) {
                Console.fine("Line ["+strLine+"]");
                if (strLine.contains(pid + " ")) {
                    Console.fine("Process "+pid+" is running");
                    return true;
                }
            }
            Console.fine("Process "+pid+"is not running");

            return false;
        } catch (Exception ex) {
            Console.debugPrint("Got exception using system command ["+command+"].");
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
}
