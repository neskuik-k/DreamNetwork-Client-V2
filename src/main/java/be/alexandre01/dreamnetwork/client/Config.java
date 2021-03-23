package be.alexandre01.dreamnetwork.client;



import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;


import java.io.*;
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
        File theDir = new File(getPath(path));
        if (theDir.exists()) {
            return true;
        }
        return false;
    }
    public static void createDir(String path){
        File theDir = new File(getPath(path));

        if (!theDir.exists()) {
           Console.print(Chalk.on("Création du dossier... ").cyan() + theDir.getName(), Level.INFO);
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
               Console.print(Chalk.on("Dossier crée").cyan(),Level.INFO);
            }
        }
    }
    public static File createFile(String path){
        File theDir = new File(getPath(path));

        if (!theDir.exists()) {
            Console.print(Chalk.on("Création du fichier... "+ theDir.getName()).cyan(), Level.INFO);
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
                Console.print(Chalk.on("Fichier crée !"), Level.INFO);
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
    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
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
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)
        ) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
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