package be.alexandre01.dreamnetwork.core.config;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.copy.CopyWithChannel;
import be.alexandre01.dreamnetwork.core.config.copy.CopyWithFiles;
import be.alexandre01.dreamnetwork.core.console.Console;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FileCopyAsync {

    final List<Operation> queue = new ArrayList<>();
    boolean running = false;
    boolean warn = false;

    List<Path> paths = new ArrayList<>();

    ICallback callback;

    ExecutorService executor = Executors.newFixedThreadPool(Main.getGlobalSettings().getThreadPoolIO());

    @Getter @Setter FileCopy copy;

    List<String> exceptFiles;

    Path source;
    Path destination;

    public FileCopyAsync(){
        String method = Main.getGlobalSettings().getCopyIOMethod();

        if(method.equalsIgnoreCase("files")){
            copy = new CopyWithFiles();
        }else if(method.equalsIgnoreCase("channels")){
            copy = new CopyWithChannel();
        }else{
            copy = new CopyWithFiles();
        }
    }
    public void execute(Path source, Path destination, ICallback callback,boolean deleteTarget,String... exceptFiles){
        execute(source,destination,callback,deleteTarget,false,exceptFiles);
    }

    private void execute(Path source, Path destination, ICallback callback,boolean deleteTarget,boolean force, String... exceptFiles){
        if(running && !force){
            queue.add(new Operation(source,destination,callback,exceptFiles,deleteTarget));
            return;
        }
    //    System.out.println("Executing copy");

        this.callback = callback;
        running = true;
        this.exceptFiles = Arrays.asList(exceptFiles);
        this.source = source;
        this.destination = destination;
        try {
            if(deleteTarget){
                deleteDirectoryAndRun(destination);
            }else {
                directoryRegister(source.toFile());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    protected void directoryRegister(File file){
        //System.out.println("Dir register");
        if(file.isDirectory()){
            try {
                Files.walk(file.toPath()).forEach(path -> {
                    try {
                        fileRegister(path.toFile());
                        //is in end
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                //    System.out.println("ENDING folder " + l);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            paste();
    }
    }

    protected void finish(){
        this.paths.clear();
        this.source = null;
        this.destination = null;
        callback.call();
        this.callback = null;
        if(queue.size() > 0){
            //System.out.println("QUEUE SIZE " + queue.size());
            Operation operation = queue.get(0);
            queue.remove(0);
            execute(operation.getSource(),operation.getDestination(),operation.getCallback(),operation.isDeleteTarget(),true,operation.getExceptFiles());
        }else{
            running = false;
        }
    }

    protected void fileRegister(File file){

        if(exceptFiles != null){
            File dir = file.getParentFile();
            // get the path from the source
            String p = dir.toString().replace(source.toString(),"");
            if(exceptFiles.contains(p+"/"+file.getName())){
                return;
            }
        }

        // adding paths to list
        paths.add(file.toPath());

        // is in end

     /*   System.out.println("ENDING file " + l);
        if(l == 0){
            System.out.println("FINISH");
        }*/
    }

    protected void paste(){
        //System.out.println("Paste !");
        // Liste pour stocker les futures des tâches de copie
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        paths = new ArrayList<>(new HashSet<>(paths));
        // Trier la liste de chemins avec un Comparator personnalisé
        paths.sort(Comparator.comparing(path -> !Files.isDirectory(path)));

        Collection<Path> folders = paths.stream().filter(Files::isDirectory).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Path> files = new ArrayList<>(paths);
        files.removeAll(folders);
        copyDirectories(folders, futures, new ICallback() {
            @Override
            public void call() {
                copyFiles(files, futures);
            }

            @Override
            public void cancel() {
                callback.cancel();
            }
        });
      //  AsynchronousFileChannel asyncChannelIn = AsynchronousFileChannel.open(in, StandardOpenOption.READ);
    }

    private void copyDirectories(Collection<Path> dir,List<CompletableFuture<Void>> futures,ICallback callback){
      //  System.out.println("copy dir");
        for (Path folder : dir) {
            //  System.out.println("copy dir " + folder);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Path destination = this.destination.resolve(source.relativize(folder));

                  //  System.out.println("Creating directory " + destination);

                    Files.createDirectories(destination);
                } catch (IOException e) {
                    Console.bug(e);
                }
            }, executor);
            futures.add(future);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRunAsync(() -> {
            //Console.fine("Every directory has been created !");
            futures.clear();
            callback.call();
            // scheduledExecutorService.shutdown();
        }, executor);
    }
    private void copyFiles(Collection<Path> files, List<CompletableFuture<Void>> futures){
        for (Path path : files) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Path destination = this.destination.resolve(source.relativize(path));

                    //System.out.println("Copying " + path + " to " + destination);

                    copy.copyFile(path, destination);

                } catch (IOException e) {
                    Console.bug(e);
                }
            }, executor);
            futures.add(future);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRunAsync(() -> {
            //System.out.println("Every task has been completed !");
            finish();
            // scheduledExecutorService.shutdown();
        }, executor);
    }

    protected void destroyDirectByteBuffer(ByteBuffer toBeDestroyed)
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
    private void deleteDirectoryAndRun(Path directory) throws IOException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try (var stream = Files.walk(directory)) {
                    stream.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }, executor);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRunAsync(() -> {
            System.out.println("Successfully deleted directory !");
            directoryRegister(source.toFile());
        }, executor);

    }


    public interface ICallback {
        void call();
        void cancel();
    }

    @AllArgsConstructor @Getter
    public class Operation{
        Path source;
        Path destination;
        ICallback callback;
        String[] exceptFiles;
        boolean deleteTarget;
    }
}