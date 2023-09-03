package be.alexandre01.dreamnetwork.core.service.screen.stream;

import java.io.OutputStream;
import java.io.PrintStream;

public class ScreenOutput extends PrintStream {
    public ScreenOutput(OutputStream out) {
        super(out);
    }

}
