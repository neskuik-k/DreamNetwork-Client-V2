package be.alexandre01.dreamnetwork.client.console;

import java.io.IOException;

import jline.Terminal;
import jline.console.ConsoleReader;

public class CustomConsoleReader extends ConsoleReader {
    public CustomConsoleReader() throws IOException {
        super();
    }

    @Override
    public Terminal getTerminal() {
        return super.getTerminal();
    }
}
