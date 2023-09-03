package be.alexandre01.dreamnetwork.core.config.copy;

import be.alexandre01.dreamnetwork.api.config.FileCopy;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CopyWithChannel implements FileCopy{
    @Override
    public boolean copyFile(Path source, Path destination) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel destinationChannel = FileChannel.open(destination, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
        {
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
            return true;
        }
        catch (IOException ex)
        {
            throw ex;
        }
    }
}
