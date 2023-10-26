package be.alexandre01.dreamnetwork.core.config.copy;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.config.FileCopy;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

public class CopyWithFiles implements FileCopy {

    private final boolean skipIfSameName;
    private final boolean overrideIfChanged;

    public CopyWithFiles(boolean skipIfSameName, boolean overrideIfChanged){
        this.skipIfSameName = skipIfSameName;
        this.overrideIfChanged = overrideIfChanged;
    }
    @Override
    public boolean copyFile(Path source, Path destination) throws IOException {
        if(Files.isDirectory(source)){
            if(!Files.exists(destination))
                Files.createDirectories(destination);
            return true;
        }

        if(Files.exists(destination) && !skipIfSameName){
            if(!Config.isWindows())
                makeWritable(destination);
            if(overrideIfChanged){
                if(Files.size(destination) == Files.size(source))
                   return true;
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }else {
                Files.delete(destination);
                if(Files.exists(destination)){
                    System.out.println(Colors.ANSI_PURPLE+"The file "+destination.getFileName()+" already exist");
                    return false;
                }
            }
        }
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }
    private void makeWritable(Path filePath) throws IOException {
        Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);

        FileAttribute<Set<PosixFilePermission>> fileAttributes =
                PosixFilePermissions.asFileAttribute(permissions);

        Files.setPosixFilePermissions(filePath, permissions);
    }
}
