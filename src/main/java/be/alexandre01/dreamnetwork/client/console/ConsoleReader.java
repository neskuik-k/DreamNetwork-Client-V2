package be.alexandre01.dreamnetwork.client.console;

import java.io.*;

public class ConsoleReader {
    public BufferedReader reader;
    public BufferedWriter writer;
    public ConsoleReader(){
        reader = new BufferedReader(new InputStreamReader(System.in));

    }
}
