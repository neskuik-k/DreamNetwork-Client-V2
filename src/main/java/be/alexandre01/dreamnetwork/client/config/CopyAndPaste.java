package be.alexandre01.dreamnetwork.client.config;

import com.google.common.base.Preconditions;
import sun.misc.Cleaner;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CopyAndPaste {
    private HashMap<Path, Long> parts;
    private HashMap<Path, AsynchronousFileChannel> fileChannels;
    private boolean cancelled;
    private long currentFiles;
    private AtomicLong filesToOperate;
    private List<Path> paths = new ArrayList<>();
    private File defaultLocation;
    private File defaultTargetLocation;
    private EstablishedAction establishedAction;
    private MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private long xmx;

    public CopyAndPaste(File sourceLocation, File targetLocation, EstablishedAction establishedAction){
        this.defaultLocation = sourceLocation;
        this.defaultTargetLocation = targetLocation;
        this.establishedAction = establishedAction;
        xmx = memoryBean.getHeapMemoryUsage().getMax();
        this.parts = new HashMap<>();
        this.fileChannels = new HashMap<>();
    }
    private void countFiles(){
        System.out.println("try");
        try (Stream<Path> files = Files.list(defaultLocation.getAbsoluteFile().toPath())) {
          currentFiles = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        filesToOperate = new AtomicLong(currentFiles);
        System.out.println(filesToOperate);
    }
    private void fetchCurrentFile(Path sourceLocation){
        System.out.println("Fetch "+ filesToOperate.get());
        System.out.println("File "+ sourceLocation);
        if (Files.isDirectory(sourceLocation)) {
            System.out.println("dir");
            fetchFilesFromDirectory(sourceLocation);
        } else {
            System.out.println("not dir");
            paths.add(sourceLocation);
        }
        System.out.println("AH1 "+ filesToOperate.get());


        filesToOperate.set(filesToOperate.get()-1);
        System.out.println("hmm" + filesToOperate.get());
        if(filesToOperate.get() == 0){
            proceedOperations();
        }
    }
    private void fetchFilesFromDirectory(Path source){
        String in = defaultLocation.getAbsolutePath();
        String out = defaultTargetLocation.getAbsolutePath();
            System.out.println("TRY OUT "+out+source.toString().substring(in.length()));
            Path outFile = Paths.get(out+source.toString().substring(in.length()));
            System.out.println("OUT>>"+outFile);

        if (!Files.exists(outFile)) {
            try {
                Files.createDirectory(outFile);
                System.out.println("CREATE DIRECTORY");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Collection<Path> list = Files.list(source).collect(Collectors.toList());
            filesToOperate.set(filesToOperate.get()+list.size());
            list.forEach(this::fetchCurrentFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void proceedOperations(){
        String in = defaultLocation.getAbsolutePath();
        String out = defaultTargetLocation.getAbsolutePath();
        System.out.println("SIZ");
        System.out.println("SIZOUNET>>" +paths.size());
        for(Path path : paths){
            System.out.println("TRY OUT "+out+path.toString().substring(in.length()));
            Path outFile = Paths.get(out+path.toString().substring(in.length()));
            System.out.println("OUT>>"+outFile);
            try {
                copyFile(path,outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void execute(){
        System.out.println("oof");
        countFiles();
        fetchCurrentFile(defaultLocation.toPath());
    }


    private void copyFile(Path in, Path target) throws IOException {

        int fileSize = (int) Files.size(in)+1;

        long allocatedMemory = xmx- (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        System.out.println("XMX "+ xmx/1024+"ko");
        System.out.println("FREE MEMORY "+ allocatedMemory/1024+"ko");
        int alloc = (int) Files.size(in)+1;
        System.out.println("FILE SIZE "+ alloc/1024+"ko");

        long position = 0;
        if(parts.containsKey(in)){
            position = parts.get(in);
        }
        alloc -= position;
        System.out.println("GET POSITION >> "+ position/1024+"ko");
        System.out.println("GET NEW ALLOC >> "+  alloc/1024+"ko");
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
                System.out.println("remove");
                parts.remove(in);
            }

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
                        Files.createFile(target);
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
                            System.out.println("Put ASYNC CHANNEL");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        return;
                    }
                }


                    asyncChannel.write(buffer, finalPosition,buffer,new CompletionHandler<Integer,ByteBuffer>(){

                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            System.out.println(result);
                            System.out.println("Complete WRITE");
                            paths.remove(in);
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

                            System.out.println("HMM 1");
                            if(parts.containsKey(in)){
                                try {
                                    System.out.println("HAVE TO COPY");
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

                            System.out.println("HMM 2");
                            System.out.println("Parts SIZE >> "+ parts.size());
                            if(!parts.isEmpty()){
                                System.out.println("Paths SIZE >> "+ paths.size());
                                System.out.println("Paths  >> "+ paths.toArray().toString());
                            if(paths.size() == 0){
                                if(cancelled){
                                    establishedAction.cancelled();
                                    return;
                                }

                                establishedAction.completed();
                                return;
                            }
                            }



                            if(paths.size() != 0){
                                String in = defaultLocation.getAbsolutePath();
                                String out = defaultTargetLocation.getAbsolutePath();

                                Path path = paths.get(0);
                                System.out.println("TRY OUT "+out+path.toString().substring(in.length()));
                                Path outFile = Paths.get(out+path.toString().substring(in.length()));
                                System.out.println("OUT>>"+outFile);
                                try {
                                    copyFile(path,outFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
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
            }
        });

    }
    public void destroyDirectByteBuffer(ByteBuffer toBeDestroyed)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {

        Preconditions.checkArgument(toBeDestroyed.isDirect(),
                "toBeDestroyed isn't direct!");

        Method cleanerMethod = toBeDestroyed.getClass().getMethod("cleaner");
        cleanerMethod.setAccessible(true);
        Object cleaner = cleanerMethod.invoke(toBeDestroyed);
        Method cleanMethod = cleaner.getClass().getMethod("clean");
        cleanMethod.setAccessible(true);
        cleanMethod.invoke(cleaner);

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