package be.alexandre01.dreamnetwork.client.console;

import jline.console.completer.Completer;

import java.io.*;
import java.util.List;

public class ConsoleReader {
    public static jline.console.ConsoleReader sReader;

    static {
        try {
            sReader = new jline.console.ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedWriter writer;
}
