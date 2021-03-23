package be.alexandre01.dreamnetwork.client.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader {
    public BufferedReader reader;
    public ConsoleReader(){
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
