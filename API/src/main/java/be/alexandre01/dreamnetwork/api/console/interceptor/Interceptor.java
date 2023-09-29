package be.alexandre01.dreamnetwork.api.console.interceptor;



import be.alexandre01.dreamnetwork.api.console.Console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class Interceptor extends PrintStream{
    public Interceptor(OutputStream out)  {
        super(out, true);
    }

    @Override
    public void print(String s) {
        try {
            if(s == null) s = "null";
            Console.print(new String(s.toString().getBytes(), "UTF-8"), Level.INFO);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void print(boolean b) {
        print(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        print(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        print(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        print(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        print(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        print(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        print(String.valueOf(s));
    }

    @Override
    public void print(Object obj) {
        print(String.valueOf(obj));
    }
}
