package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingHandler;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import be.alexandre01.dreamnetwork.core.websocket.sessions.tests.OverViewTest;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;

public class OverViewFrame extends FrameAbstraction {
    public OverViewFrame(WebSession session) {
        super(session, "overview");
        setTester(new OverViewTest(this));
    }

    @Override
    public void onEnter() {
        // Nothing to do here
        sendCurrentNodeStatistics();
    }

    @Override
    public void onLeave() {
        // Nothing to do here
    }


    @Override
    public void handle(WebMessage webMessage) {
        // Nothing to do here
    }

    public void sendCurrentNodeStatistics() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        double cpuUsage;
        // calculate cpu usage

        // What % CPU load this current JVM is taking, from 0.0-1.0
        System.out.println(osBean.getProcessCpuLoad());
        cpuUsage = osBean.getProcessCpuLoad();
        long ramUsage = osBean.getFreePhysicalMemorySize();
        long ramTotal = osBean.getTotalPhysicalMemorySize();
        long diskTotal = 0;
        long diskFree = 0;
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                diskTotal += store.getTotalSpace();
                diskFree += store.getUsableSpace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // calculate disk usage
        long networkUsage = ByteCountingHandler.getTotalBytesRead();
        // calculate network usage

        WebMessage webMessage = new WebMessage();
        webMessage.put("cpuUsage", cpuUsage);
        webMessage.put("ramUsage", ramUsage);
        webMessage.put("ramTotal", ramTotal);
        webMessage.put("diskFree", diskFree);
        webMessage.put("diskTotal", diskTotal);
        webMessage.put("networkUsage", networkUsage);
        System.out.println("Sending overview frame");
        System.out.println(getSession() + " yey");
        getSession().send(webMessage);
    }
}
