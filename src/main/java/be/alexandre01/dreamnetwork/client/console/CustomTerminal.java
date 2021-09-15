package be.alexandre01.dreamnetwork.client.console;

import jline.DefaultTerminal2;
import jline.Terminal;
import jline.UnixTerminal;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomTerminal extends UnixTerminal {
    public CustomTerminal() throws Exception {
        super();
    }

    @Override
    public void disableInterruptCharacter() {
        System.out.println("Fuck");
        super.disableInterruptCharacter();
    }
}
