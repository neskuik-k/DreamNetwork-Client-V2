package be.alexandre01.dreamnetwork.core.console.formatter;


import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.interceptor.Interceptor;
import be.alexandre01.dreamnetwork.core.console.logging.LoggingOutputStream;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

@SuppressWarnings("unused")
public class Formatter {
    PrintStream defaultStream;
    @Getter ConciseFormatter conciseFormatter;
    @Getter ConciseFormatter defaultFormatter;
    public  PrintStream prStr;
    public void format(){
        Ansi.setEnabled(true);

        defaultStream = System.out;
        ByteArrayOutputStream loggerContent = new LoggingOutputStream(Core.getLogger(), Level.ALL);
        prStr = null;
        prStr = new Interceptor(loggerContent);

        StreamHandler streamHandler = new StreamHandler(prStr, conciseFormatter = new ConciseFormatter(false));

        final PrintStream err = System.err;
        Core.getLogger().setUseParentHandlers(false);
        try {
            defaultFormatter = new ConciseFormatter(true);
            ConsoleHandler handler = new ConsoleHandler();
            try {
                handler.setEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            handler.setFormatter(defaultFormatter);
            handler.setLevel(Level.ALL);
            handler.flush();
            Core.getLogger().addHandler( handler);
            System.setOut(prStr);
            System.setErr(System.out);
        if(!Core.getInstance().isDebug()){
            Core.getLogger().setLevel(Level.INFO);
        }else {
            Core.getLogger().setLevel(Level.FINER);
        }



        } finally {
            System.setErr(err);
        }
    }

    public PrintStream getDefaultStream() {
        return defaultStream;
    }

    public PrintStream getPrStr() {
        return prStr;
    }
}
