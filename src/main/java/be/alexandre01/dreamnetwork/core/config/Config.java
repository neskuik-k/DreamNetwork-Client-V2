package be.alexandre01.dreamnetwork.core.config;



import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;

public class Config {
    public static String getPath(String path){
        String resourcePath = null;
        if(System.getProperty("os.name").startsWith("Windows")){
            resourcePath = path.replaceAll("/","\\\\");
        }else {
            resourcePath = path;
        }
        return resourcePath;
    }
    public static boolean contains(String path){
        Path theDir = Paths.get(getPath(path));
        //File theDir = new File(getPath(path));
        /*if (theDir.exists()) {
            return true;
        }*/
        if(Files.exists(theDir))
            return true;

        return false;
    }
    public static void createDir(String path){
        createDir(path,true);
    }
    public static void createDir(String path,boolean notify){
        File theDir = new File(getPath(path));

        if (!theDir.exists()) {
            if(notify)
                Console.print(Chalk.on("Creating the directory... ").cyan() + theDir.getName(), Level.INFO);
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result && notify) {
                Console.print(Chalk.on("Directory created").cyan(),Level.INFO);
            }
        }
    }
    public static File createFile(String path){
        File theDir = new File(getPath(path));

        if (!theDir.exists()) {
            Console.print(Chalk.on("Creating the file...  "+ theDir.getName()).cyan(), Level.INFO);
            boolean result = false;

            try{
                try {
                    theDir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                Console.print(Chalk.on("File created"), Level.INFO);
            }
        }
        return theDir;
    }
    public static boolean removeDir(String path){
        while (contains(path)){
            File theDir = new File(getPath(path));

            if (theDir.exists()) {
                File[] allContents = theDir.listFiles();
                if (allContents != null) {
                    for (File file : allContents) {
                        removeDir(file.getAbsolutePath());
                    }
                }
                return theDir.delete();

            }
        }
        return false;

    }
    public static void asyncCopy(File sourceLocation, File targetLocation,EstablishedAction establishedAction,String... exceptFile) throws IOException {
        CopyAndPaste copyAndPaste = new CopyAndPaste(sourceLocation,targetLocation,establishedAction,exceptFile);
        Thread thread = new Thread(copyAndPaste);
        thread.run();
    }
    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }
    public static void write(InputStream in, File targetLocation) throws IOException {

        if(!targetLocation.exists())
            targetLocation.createNewFile();
        try (
                OutputStream out = new FileOutputStream(targetLocation)
        ) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
            out.flush();
            out.close();
        }
    }
    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private static void copyFile(File source, File target) throws IOException {

        Path in = Paths.get(source.toURI());
        ByteBuffer buffer = ByteBuffer.allocate(1024);
       AsynchronousFileChannel asyncChannelIn = AsynchronousFileChannel.open(in, StandardOpenOption.READ);
       asyncChannelIn.read(buffer,0,buffer,new CompletionHandler<Integer,ByteBuffer>(){
           int pos = 0;
           @Override
           public void completed(Integer result, ByteBuffer attachment) {
               // if result is -1 means nothing was read.
               if (result != -1) {
                   pos += result;  // don't read the same text again.
                   // your output command.
                   System.out.println(new String(buffer.array()));

                   // reset the buffer so you can read more.

               }
               // initiate another asynchronous read, with this.
               buffer.flip();
               Path out = Paths.get(target.toURI());

               try {
                   Files.deleteIfExists(target.getAbsoluteFile().toPath());
                   Files.createFile(target.getAbsoluteFile().toPath());
               } catch (IOException e) {
                   e.printStackTrace();
               }
               try(AsynchronousFileChannel asyncChannel = AsynchronousFileChannel.open(out, StandardOpenOption.WRITE)){
                   asyncChannel.write(buffer,0,buffer,new CompletionHandler<Integer,ByteBuffer>(){

                       @Override
                       public void completed(Integer result, ByteBuffer attachment) {
                           System.out.println(result);
                           System.out.println("Complete WRITE");
                           buffer.clear();
                       }

                       @Override
                       public void failed(Throwable exc, ByteBuffer attachment) {
                           System.out.println("failed");
                           buffer.clear();
                       }
                   });
               } catch (IOException e) {
                   e.printStackTrace();
               }

               //System.out.println(new String(buffer.array()).trim());
               //System.out.println(new String( buffer.get(attachment.array()).array()).trim());

             //  attachment.read(buffer, pos , attachment, this );
           }

           @Override
           public void failed(Throwable exc, ByteBuffer attachment) {

           }
       });




        }

    public static String pathConvert(String path){
        if(System.getProperty("os.name").startsWith("Windows")){
            return path.replaceAll("/","\\\\");
        }else {
            return path;
        }

    }

    public static ArrayList<String> getGroupsLines(String file)throws IOException{
        String fileSeparator = System.getProperty("file.separator");
        File serverFile = new File(pathConvert(file));
        BufferedReader br = new BufferedReader(new FileReader(serverFile.getAbsolutePath()));
        try {

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            ArrayList<String> lines = new ArrayList<>();
            while (line != null) {
                if(!line.replaceAll(" ","").startsWith("#"))
                lines.add(line);
                line = br.readLine();
            }
            return lines;


        } finally {
            br.close();
        }


    }
    public static boolean isWindows(){
        if(System.getProperty("os.name").startsWith("Windows")){
          return true;
        }else {
            return false;
        }
    }

}