package be.alexandre01.dreamnetwork.client.console;

import java.io.*;

public class ConsoleReader {
    public jline.console.ConsoleReader reader;
    public BufferedWriter writer;
    public ConsoleReader(){
        try {
            reader = new jline.console.ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
