package be.alexandre01.dreamnetwork.core.config;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CopyAndPaste extends Thread{
    private HashMap<Path, Long> parts;
    private HashMap<Path, AsynchronousFileChannel> fileChannels;
    private boolean cancelled;
    private long currentFiles;
    private boolean warn = false;
    private AtomicLong filesToOperate;
    private List<Path> paths = new ArrayList<>();
    private File defaultLocation;
    private File defaultTargetLocation;
    private EstablishedAction establishedAction;
    private String[] exeptFile;
    private MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private long xmx;
    public boolean hasReached = false;
    AtomicBoolean b = new AtomicBoolean(false);

    public CopyAndPaste(File sourceLocation, File targetLocation, EstablishedAction establishedAction,String... exeptFile){
        this.defaultLocation = sourceLocation;
        this.defaultTargetLocation = targetLocation;
        this.establishedAction = establishedAction;
        this.exeptFile = exeptFile;
        xmx = memoryBean.getHeapMemoryUsage().getMax();
        this.parts = new HashMap<>();
        this.fileChannels = new HashMap<>();
    }


    private Collection<Path> countFiles(Path path){
        Collection<Path> paths = null;
        try (Stream<Path> files = Files.list(path)) {

          paths = files.collect(Collectors.toList());
          currentFiles = paths.size();
            

        } catch (IOException e) {
            e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
        }
        if(filesToOperate == null){
            filesToOperate = new AtomicLong(0);
        }

        filesToOperate.set(filesToOperate.get()+currentFiles);
        return paths;
    }
    private void fetchCurrentFile(Path sourceLocation){
        if (Files.isDirectory(sourceLocation)) {
            fetchFilesFromDirectory(sourceLocation);
        } else {
            boolean result = Arrays.stream(exeptFile).anyMatch(sourceLocation.getFileName().toString()::equals);
            if(!result){
                paths.add(sourceLocation);
            }

        }

        filesToOperate.decrementAndGet();
        if(filesToOperate.get() == 0){
            proceedOperations();
        }
    }
    private void fetchFilesFromDirectory(Path source){
        String in = defaultLocation.getAbsolutePath();
        String out = defaultTargetLocation.getAbsolutePath();
            Path outFile = Paths.get(out+source.toString().substring(in.length()));

        if (!Files.exists(outFile)) {
            try {
                Files.createDirectory(outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collection<Path> list = countFiles(source);
        list.forEach(this::fetchCurrentFile);



    }
    private void proceedOperations(){
        String in = defaultLocation.getAbsolutePath();
        String out = defaultTargetLocation.getAbsolutePath();
        for(Path path : paths){
            Path outFile = Paths.get(out+path.toString().substring(in.length()));
            try {
                copyFile(path,outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        fetchFilesFromDirectory(defaultLocation.getAbsoluteFile().toPath());
    }


    private void copyFile(Path in, Path target) throws IOException {

        int fileSize = (int) Files.size(in)+1;

        long allocatedMemory = xmx- (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
       // System.out.println("XMX "+ xmx/1024+"ko");
       // System.out.println("FREE MEMORY "+ allocatedMemory/1024+"ko");
        int alloc = (int) Files.size(in)+1;
       // System.out.println("FILE SIZE "+ alloc/1024+"ko");

        long position = 0;
        if(parts.containsKey(in)){
            position = parts.get(in);
        }

        alloc -= position;
       // System.out.println("GET POSITION >> "+ position/1024+"ko");
        //System.out.println("GET NEW ALLOC >> "+  alloc/1024+"ko");
        if(alloc > xmx/8){
            System.out.println("TROP GROS");
            alloc = (int) (xmx/8)+1;
            long pAllocated = 0;
            if(parts.containsKey(in)){
                pAllocated = parts.get(in);
            }
            System.out.println("OldAlloc>> "+ pAllocated+" + " + alloc);
            System.out.println("OldAlloc= "+ (pAllocated+alloc));
            parts.put(in,(pAllocated+alloc));
        }else {
            if(parts.containsKey(in)){
                parts.remove(in);
            }

        }

        if(alloc > xmx){
            System.out.println(in.getFileName()+" is too fat!");
            parts.remove(in);
            fileChannels.remove(in);
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(alloc);
        AsynchronousFileChannel asyncChannelIn = AsynchronousFileChannel.open(in, StandardOpenOption.READ);
        long finalPosition = position;
        asyncChannelIn.read(buffer,0,buffer,new CompletionHandler<Integer,ByteBuffer>(){
            int pos = 0;
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // if result is -1 means nothing was read.
                if (result != -1) {
                    pos += result;  // don't read the same text again.
                    // your output command.

                    // reset the buffer so you can read more.
                    parts.remove(in);
                    fileChannels.remove(in);
                }
                // initiate another asynchronous read, with this.
                buffer.flip();


                try {
                    if (!Files.exists(target)){
                        try {
                            Files.createFile(target);
                        }catch (FileAlreadyExistsException f){
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AsynchronousFileChannel asyncChannel;
                if(fileChannels.containsKey(in)){
                    asyncChannel = fileChannels.get(in);
                }else {
                    try {
                        asyncChannel = AsynchronousFileChannel.open(target, StandardOpenOption.WRITE);
                        if(parts.containsKey(in)){
                            fileChannels.put(in,asyncChannel);
                          //  System.out.println("Put ASYNC CHANNEL");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        return;
                    }
                }


                    asyncChannel.write(buffer, finalPosition,buffer,new CompletionHandler<Integer,ByteBuffer>(){

                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            // System.out.println("Complete WRITE");
                            paths.remove(in);
                           buffer.clear();
                           //DEPRECATED IN JDK 9+
                            try {
                                destroyDirectByteBuffer(buffer);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }

                            if(parts.containsKey(in)){
                                try {
                                    copyFile(in,target);
                                    return;
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                            }else {
                                if(fileChannels.containsKey(in)){
                                    fileChannels.remove(in);
                                }
                                try {
                                    asyncChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                           // System.out.println("Parts SIZE >> "+ parts.size());
                            if(!parts.isEmpty()){
                                //System.out.println("Paths  >> "+ paths.toArray().toString());
                            }
                            if(paths.size() == 0){
                                if(hasReached)
                                    return;

                                hasReached = true;
                                if(cancelled){
                                    if(warn){
                                        System.out.println(Colors.RED_BOLD+"java --add-opens are not set please add this argument before -jar");
                                        System.out.println(Colors.RED_BOLD+"--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                                    }
                                    establishedAction.cancelled();
                                    return;
                                }
                                if(warn){
                                    System.out.println(Colors.RED_BOLD+"java --add-opens are not set please add this argument before -jar");
                                    System.out.println(Colors.RED_BOLD+"--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                                }
                                CopyAndPaste.this.stop();
                                if(b.get())
                                    return;
                                paths.clear();
                                b.set(true);
                                establishedAction.completed();

                                return;
                            }

                                String in = defaultLocation.getAbsolutePath();
                                String out = defaultTargetLocation.getAbsolutePath();

                                Path path = paths.get(0);
                              //  System.out.println(">> "+path);
                                if(path == null && !cancelled){
                                    if(b.get())
                                        return;
                                    paths.clear();
                                    b.set(true);
                                    establishedAction.completed();
                                    CopyAndPaste.this.stop();
                                    if(warn){
                                        System.out.println(Colors.RED_BOLD+"java --add-opens are not set please add this argument before -jar");
                                        System.out.println(Colors.RED_BOLD+"--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                                    }
                                    cancelled = true;
                                    return;
                                }
                                try {
                              //  System.out.println("TRY OUT "+out+path.toString().substring(in.length()));
                                Path outFile = Paths.get(out+path.toString().substring(in.length()));
                            //   System.out.println("OUT>>"+outFile);
                                    copyFile(path,outFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    if(b.get())
                                        return;
                                    paths.clear();
                                    b.set(true);
                                    establishedAction.cancelled();
                                    if(warn){
                                        System.out.println("java --add-opens are not set please add this argument before -jar");
                                        System.out.println("--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                                    }
                                }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            System.out.println(exc.toString());
                            exc.printStackTrace();
                            System.out.println(exc.getCause().toString());
                            System.out.println("failed");
                            cancelled = true;
                            buffer.clear();


                            try {
                                destroyDirectByteBuffer(buffer);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }


                            if(paths.size() == 0){
                                establishedAction.cancelled();
                                if(warn){
                                    System.out.println("java --add-opens are not set please add this argument before -jar");
                                    System.out.println("--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                                }
                            }
                        }
                    });


                //System.out.println(new String(buffer.array()).trim());
                //System.out.println(new String( buffer.get(attachment.array()).array()).trim());

                //  attachment.read(buffer, pos , attachment, this );
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("failed read");
                buffer.clear();


                try {
                    destroyDirectByteBuffer(buffer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }


                paths.clear();
                establishedAction.cancelled();
                if(warn){
                    System.out.println(Colors.RED_BOLD_BRIGHT+"java --add-opens are not set please add this argument before -jar");
                    System.out.println(Colors.RED_BOLD_BRIGHT+"--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED");
                }
            }
        });

    }
    public void destroyDirectByteBuffer(ByteBuffer toBeDestroyed)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {

        try {
            Preconditions.checkArgument(toBeDestroyed.isDirect(),
                    "toBeDestroyed isn't direct!");

            Method cleanerMethod = toBeDestroyed.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(toBeDestroyed);

            Method cleanMethod = cleaner.getClass().getMethod("clean");
            cleanMethod.setAccessible(true);
            cleanMethod.invoke(cleaner);
        }catch (Exception e){
            if(e.getClass().getSimpleName().equalsIgnoreCase("InaccessibleObjectException")){
                warn = true;
            }
        }
    }
    public static void destroyBuffer(Buffer buffer) {
        if(buffer.isDirect()) {
            try {
                if(!buffer.getClass().getName().equals("java.nio.DirectByteBuffer")) {
                    Field attField = buffer.getClass().getDeclaredField("att");
                    attField.setAccessible(true);
                    buffer = (Buffer) attField.get(buffer);
                }

                Method cleanerMethod = buffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer);
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(cleaner);
            } catch(Exception e) {
                System.out.println("Could not free the ram !");
            }
        }
    }
}