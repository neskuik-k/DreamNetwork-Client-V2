package be.alexandre01.dreamnetwork.client.console;

import jline.console.completer.Completer;

import java.io.*;
import java.util.List;

public class ConsoleReader {
    public static jline.console.ConsoleReader sReader;
    public jline.console.ConsoleReader reader;
    public BufferedWriter writer;
    public ConsoleReader(){
        try {
            sReader = reader = new jline.console.ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
