package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCounting;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingInboundHandler;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import be.alexandre01.dreamnetwork.core.websocket.sessions.tests.OverViewTest;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;

public class OverViewFrame extends FrameAbstraction {
    public OverViewFrame(WebSession session) {
        super(session, "overview");
        setTester(new OverViewTest(this));
        setRefreshRate(1000*5);
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
        cpuUsage = osBean.getSystemCpuLoad();
        long freeMem = osBean.getFreePhysicalMemorySize();
        long ramTotal = osBean.getTotalPhysicalMemorySize();

        long usedMemory = ramTotal - freeMem;
        long fullRamUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        MemoryUsage nonheap = memBean.getNonHeapMemoryUsage();
        long diskTotal = 0;
        long diskFree = 0;

        FileStore fileStore;
        if(Config.isWindows()){
            try {
                fileStore = Files.getFileStore(FileSystems.getDefault().getPath("C:"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            try {
                fileStore = Files.getFileStore(FileSystems.getDefault().getPath("/"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            diskTotal = fileStore.getTotalSpace();
            diskFree = fileStore.getUsableSpace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // calculate disk usage
        long networkUsageIN = ByteCounting.getTotalBytesRead();
        long networkUsageOUT = ByteCounting.getTotalBytesWrite();
          long networkUsagePerSecondOut = ByteCounting.getLatestTotalBytesPerSecond(ByteCounting.Type.OUTBOUND);
        long networkUsagePerMinuteOut = ByteCounting.getLatestTotalBytesPerMinute(ByteCounting.Type.OUTBOUND);
        long networkUsagePerSecondIn = ByteCounting.getLatestTotalBytesPerSecond(ByteCounting.Type.INBOUND);
        long networkUsagePerMinuteIn = ByteCounting.getLatestTotalBytesPerMinute(ByteCounting.Type.INBOUND);

        System.out.println("networkUsagePerSecondIn : " + networkUsagePerSecondIn);

       // System.out.println(Arrays.toString(ByteCounting.getTotalBytesPerSecond(ByteCounting.Type.OUTBOUND)));

        // calculate network usage

        WebMessage webMessage = new WebMessage();
        webMessage.put("cpuUsage", cpuUsage);
        webMessage.put("ramDN", "ramDN");
        webMessage.put("ramUsage", usedMemory);
        webMessage.put("ramTotal", ramTotal);
        webMessage.put("diskFree", diskFree);
        webMessage.put("diskTotal", diskTotal);
        webMessage.put("networkUsageOUT", networkUsageOUT);
        webMessage.put("networkUsageIN", networkUsageIN);
        webMessage.put("networkUsagePerSecondIN", networkUsagePerSecondIn);
        webMessage.put("networkUsagePerMinuteIN", networkUsagePerMinuteIn);
        webMessage.put("networkUsagePerSecondOUT", networkUsagePerSecondOut);
        webMessage.put("networkUsagePerMinuteOUT", networkUsagePerMinuteOut);

        System.out.println("Sending overview frame");
        System.out.println(getSession() + " yey");
        getSession().send(webMessage);
    }

    @Override
    public void refresh() {
        sendCurrentNodeStatistics();
    }
}
