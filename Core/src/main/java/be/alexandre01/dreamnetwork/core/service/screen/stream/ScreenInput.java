package be.alexandre01.dreamnetwork.core.service.screen.stream;



import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ScreenInput extends ByteArrayInputStream {
    public InputStream inputStream;

    public ScreenInput(byte[] buf) {
        super(buf);
    }
}
