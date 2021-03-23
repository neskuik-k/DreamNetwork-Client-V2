package be.alexandre01.dreamnetwork.client.console.interceptor;



import be.alexandre01.dreamnetwork.client.console.Console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class Interceptor extends PrintStream{
        public Interceptor(OutputStream out)  {
            super(out, true);
        }


        @Override
        public void print(String s)
        {//do what ever you like
            try {
                Console.print(new String(s.getBytes(), "UTF-8"), Level.INFO);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
}
