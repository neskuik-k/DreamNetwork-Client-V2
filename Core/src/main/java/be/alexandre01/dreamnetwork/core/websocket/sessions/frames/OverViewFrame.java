package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.Core;
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
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class OverViewFrame extends FrameAbstraction {
    public static void main(String[] args) {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        long freeMem = osBean.getFreePhysicalMemorySize();
        long ramTotal = osBean.getTotalPhysicalMemorySize();

        long usedMemory = ramTotal - freeMem;
        // REMOVE cache memory
        long fullRamUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        MemoryUsage nonheap = memBean.getNonHeapMemoryUsage();
        System.out.println("freeMem : " + freeMem/1024);
        System.out.println("ramTotal : " + ramTotal/1024);
        System.out.println("usedMemory : " + usedMemory/1024);
        System.out.println("fullRamUsage : " + fullRamUsage/1024);
        System.out.println("heap : " + heap);
        System.out.println("nonheap : " + nonheap);
        long cachedMemory = osBean.getCommittedVirtualMemorySize() - osBean.getFreePhysicalMemorySize();
        System.out.println("cachedMemory : " + osBean.getCommittedVirtualMemorySize()/1024);
    }
    boolean[] refreshs = new boolean[]{false,false,false,false};
    // 0 is IN SEC
    // 1 IS OUT SEC
    // 2 IS IN MIN
    // 3 IS OUT MIN
    Consumer<ByteCounting.ByteCountingData> newLong = new Consumer<ByteCounting.ByteCountingData>() {
        @Override
        public void accept(ByteCounting.ByteCountingData data) {

         //   System.out.println("new bytes");
            String time = data.getTime().equals(ByteCounting.Time.SECONDS) ? "seconds" : "minutes";
            String type = data.getType().equals(ByteCounting.Type.INBOUND) ? "in" : "out";
            if(data.getTime().equals(ByteCounting.Time.SECONDS)){
                if(data.getType().equals(ByteCounting.Type.INBOUND)){
                    refreshs[0] = true;
                }else{
                    refreshs[1] = true;
                }
            }else {
                if(data.getType().equals(ByteCounting.Type.INBOUND)){
                    refreshs[1] = true;
                }else{
                    refreshs[3] = true;
                }
            }


        }
    };
    public OverViewFrame(WebSession session) {
        super(session, "overview");
        setTester(new OverViewTest(this));


    }

    @Override
    public void onEnter() {
        setUpdateRate(1000*5);
        // Nothing to do here
        sendCurrentNodeStatistics();
        // sending bytes
        runTask(() -> {
            getSession().send(Objects.requireNonNull(new WebMessage()
                            // network
                            .put("bytesINSec", ByteCounting.getTotalBytesPerSecond(ByteCounting.Type.INBOUND))
                            .put("bytesOUTSec", ByteCounting.getTotalBytesPerSecond(ByteCounting.Type.OUTBOUND))
                            .put("bytesINMin", ByteCounting.getTotalBytesPerMinute(ByteCounting.Type.INBOUND))
                            .put("bytesOUTMin", ByteCounting.getTotalBytesPerMinute(ByteCounting.Type.OUTBOUND))
                            // players
                            .put("playersMin", Core.getInstance().getServicePlayersManager().getMinutedHistory().getTable())
                            .put("playersHour", Core.getInstance().getServicePlayersManager().getHourHistory().getTable()))
            );

        },10000);



       // ByteCounting.registerConsumer(newLong);


    }

    @Override
    public void onLeave() {
        // Nothing to do here
       // ByteCounting.unregisterConsumer(newLong);
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
        cpuUsage = osBean.getSystemCpuLoad();
        long freeMem = osBean.getFreePhysicalMemorySize();
        long ramTotal = osBean.getTotalPhysicalMemorySize();

        long usedMemory = ramTotal - freeMem;
        // REMOVE cache memory
      //  usedMemory = usedMemory - (osBean.getTotalPhysicalMemorySize() - osBean.getTotalSwapSpaceSize());
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
        if(refreshs[0]){
            webMessage.put("networkUsagePerSecondIN", networkUsagePerSecondIn);
        }
        if(refreshs[1]){
            webMessage.put("networkUsagePerSecondOUT", networkUsagePerSecondOut);
        }
        if(refreshs[2]){
            webMessage.put("networkUsagePerMinuteIN", networkUsagePerMinuteIn);
        }
        if(refreshs[3]){
            webMessage.put("networkUsagePerMinuteOUT", networkUsagePerMinuteOut);
        }




        //System.out.println("Sending overview frame");
        //System.out.println(getSession() + " yey");
        //System.out.println("Is open ? " + getSession().getChannelHandlerContext().channel().isOpen());
        //System.out.println("Is open ? " + getSession().getChannelHandlerContext().channel().isActive());
        getSession().send(webMessage);
    }

    @Override
    public void refresh() {
        sendCurrentNodeStatistics();
    }
}
