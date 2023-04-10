package be.alexandre01.dreamnetwork.core.utils.sockets;

import be.alexandre01.dreamnetwork.core.console.Console;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortUtils {
    public static boolean isAvailable(int port, boolean isSilent) {
        if(!isSilent)
            Console.printLang("core.utils.sockets.checkingPort", port);
        if (port < 1 || port > 65535) {
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
