package be.alexandre01.dreamnetwork.api.service.screen;

public interface IScreenInReader extends Runnable {

    java.util.List<ReaderLine> getReaderLines();

    public static interface ReaderLine {
        String readLine(String line);
    }
}
