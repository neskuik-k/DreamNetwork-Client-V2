package be.alexandre01.dreamnetwork.utils;


import java.io.IOException;
import java.io.InputStream;

public class ImpatientInputStream extends InputStream {

    private final InputStream in;
    private boolean eof;

    public ImpatientInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        if (eof) {
            return -1;
        }
        if (available() == 0) {
            eof = true;
            return -1;
        }
        return in.read();
    }

}