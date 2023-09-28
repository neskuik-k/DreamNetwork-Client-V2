package be.alexandre01.dreamnetwork.api.config;

import java.nio.file.Path;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:30
*/
public interface IFileCopyAsync {
    /*
    Execute a File copy in Async
     */
    void execute(Path source, Path destination, ICallback callback, boolean deleteTarget, String... exceptFiles);

    FileCopy getCopy();

    void setCopy(FileCopy copy);

    public interface ICallback {
        void call();

        void cancel();
    }
}
