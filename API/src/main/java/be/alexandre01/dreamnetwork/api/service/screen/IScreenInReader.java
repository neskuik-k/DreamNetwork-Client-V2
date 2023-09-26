package be.alexandre01.dreamnetwork.api.service.screen;

import java.util.logging.FileHandler;

public interface IScreenInReader extends Runnable {

    java.util.List<ReaderLine> getReaderLines();

    FileHandler getFileHandler();

    public static interface ReaderLine {
        String readLine(String line);
    }
}
