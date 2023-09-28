package be.alexandre01.dreamnetwork.api.config;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface FileCopy {
public boolean copyFile(Path source,Path destination) throws IOException;

  /*  public static void main(String[] args) {
        Path sourceFile = Path.of("path/to/source/file");
        Path destinationFile = Path.of("path/to/destination/file");

        try {
            copyFile(sourceFile, destinationFile);
            System.out.println("File copied successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while copying the file: " + e.getMessage());
        }
    }*/
}