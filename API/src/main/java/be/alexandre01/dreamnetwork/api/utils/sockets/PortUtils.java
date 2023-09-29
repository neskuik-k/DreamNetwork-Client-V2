package be.alexandre01.dreamnetwork.api.utils.sockets;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.console.Console;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.logging.Level;

public class PortUtils {
    public static boolean isAvailable(int port, boolean isSilent) {
        GlobalSettings globalSettings = DNUtils.get().getConfigManager().getGlobalSettings();
        if(!globalSettings.isFindAllocatedPorts()){
            return true;
        }
        if(!isSilent){
            Console.printLang("core.utils.sockets.checkingPort", port);
        }else {
            Console.sendToLog("Checking "+port+"...", Level.FINE,"global");
        }

        if (globalSettings.getPortRangeInt()[0] < 1 || port > globalSettings.getPortRangeInt()[1]) {
            throw new IllegalArgumentException(Console.getFromLang("core.utils.sockets.invalidStartPort", port));
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
            Console.fine("Port "+port+" is already in use, trying "+(port+1)+"...");
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
