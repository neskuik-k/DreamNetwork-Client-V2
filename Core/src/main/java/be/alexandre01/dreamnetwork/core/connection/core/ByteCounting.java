package be.alexandre01.dreamnetwork.core.connection.core;

import be.alexandre01.dreamnetwork.api.utils.buffers.FixedSizeRingBuffer;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;


public class ByteCounting {

    private static final HashSet<Consumer<ByteCountingData>> listeners = new HashSet<>();
    @Getter
    private long bytesRead = 0;
    private static long bytesReadPerMin = 0;
    private static long bytesReadPerSec = 0;

    private static long bytesWritePerMin = 0;
    private static long bytesWritePerSec = 0;

    private static final FixedSizeRingBuffer<Long> bytesReadPerMinTables = new FixedSizeRingBuffer<>(new Long[60]);

    private static final FixedSizeRingBuffer<Long> bytesReadPerSecTables = new FixedSizeRingBuffer<>(new Long[60]);

    private static final FixedSizeRingBuffer<Long> bytesWritePerMinTables = new FixedSizeRingBuffer<>(new Long[60]);

    private static final FixedSizeRingBuffer<Long> bytesWritePerSecTables = new FixedSizeRingBuffer<>(new Long[60]);


    @Getter
    private static long timeLastForMinute = System.currentTimeMillis();
    @Getter
    private static long timeLastForSeconds = System.currentTimeMillis();

    // private static final ResourceLeakDetector<ByteCountingHandler> leakDetector = ResourceLeakDetector.newInstance(ByteCountingHandler.class);
    @Getter
    private static long totalBytesRead = 0;

    @Getter
    private static long totalBytesWrite = 0;

    public void newBytes(long length, Type type) {
        bytesRead += length;


        if (type == Type.INBOUND) {
            totalBytesRead += length;
            bytesReadPerMin += length;
            bytesReadPerSec += length;
          //  System.out.println("New wow");
        } else {
            totalBytesWrite += length;
            bytesWritePerMin += length;
            bytesWritePerSec += length;
        }



       // System.out.println("bytesRead : " + bytesRead + " totalBytesRead : " + totalBytesRead);

        long time = System.currentTimeMillis();
        if (time - timeLastForMinute >= 1000 * 60) {
            //System.out.println("time : " + time + " timeLastForMinute : " + timeLastForMinute);
                bytesReadPerMinTables.fill(bytesReadPerMin);
                if(!listeners.isEmpty()){
                    ByteCountingData data = new ByteCountingData(type, bytesReadPerMin, Time.MINUTE);
                    listeners.forEach(byteCountingDataConsumer -> byteCountingDataConsumer.accept(data));
                }
                bytesReadPerMin = 0;
               // System.out.println("Filling bytesWritePerMin : " + bytesWritePerMin);
                bytesWritePerMinTables.fill(bytesWritePerMin);
             // System.out.println("bytesWritePerMinTables : " + Arrays.toString(bytesWritePerMinTables.getTable()));
                bytesWritePerMin = 0;
                timeLastForMinute = time;
        }
        if (time - timeLastForSeconds >= 1000) {
           // System.out.println("time : " + time + " timeLastForSeconds : " + (time-timeLastForSeconds));
                bytesReadPerSecTables.fill(bytesReadPerSec);
                if(!listeners.isEmpty()){
                    ByteCountingData data = new ByteCountingData(type, bytesReadPerSec, Time.SECONDS);
                   // System.out.println(listeners.size());
                    listeners.forEach(byteCountingDataConsumer -> byteCountingDataConsumer.accept(data));
                }
               // System.out.println("Filled readPerSec : " + bytesReadPerSec);
                bytesReadPerSec = 0;
                //System.out.println("Filling bytesWritePerSec : " + bytesWritePerSec);
                bytesWritePerSecTables.fill(bytesWritePerSec);
               // System.out.println("bytesWritePerSecTables : " + Arrays.toString(bytesWritePerSecTables.getTable()));
                bytesWritePerSec = 0;
            timeLastForSeconds = time;
        }

    }

    public static Long[] getTotalBytesPerSecond(Type type) {
        return type == Type.INBOUND ? bytesReadPerSecTables.getTable() : bytesWritePerSecTables.getTable();
    }

    public static Long getLatestTotalBytesPerSecond(Type type) {
        return type == Type.INBOUND ? bytesReadPerSecTables.getLast().orElse(-1L) : bytesWritePerSecTables.getLast().orElse(-1L);
    }

    public static Long[] getTotalBytesPerMinute(Type type) {
        return type == Type.INBOUND ? bytesReadPerMinTables.getTable() : bytesWritePerMinTables.getTable();
    }

    public static Long getLatestTotalBytesPerMinute(Type type) {
        return type == Type.INBOUND ? bytesReadPerMinTables.getLast().orElse(-1L) : bytesWritePerMinTables.getLast().orElse(-1L);
    }

    public static void registerConsumer(Consumer<ByteCountingData> newLong){
        System.out.println("register");
        listeners.add(newLong);
    }

    public static void unregisterConsumer(Consumer<ByteCountingData> consumer){
        listeners.remove(consumer);
    }


    public static Long getTotalBytesRead(Type type) {
        return type == Type.INBOUND ? totalBytesRead : totalBytesWrite;
    }

    public enum Type {
        INBOUND,
        OUTBOUND
    }

    public enum Time{
        SECONDS,
        MINUTE;
    }

    @Getter
    public static class ByteCountingData{
        Type type;
        long bytes;
        Time time;
        ByteCountingData(Type type, long bytes, Time time ){
            this.bytes = bytes;
            this.type = type;
            this.time = time;
        }
    }
}