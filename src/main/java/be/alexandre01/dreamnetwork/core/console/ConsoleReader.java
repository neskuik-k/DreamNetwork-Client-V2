package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.widgets.BlockMod;
import be.alexandre01.dreamnetwork.core.console.widgets.DebugMod;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConsoleReader {
    public static LineReader sReader;
    public static Terminal terminal;
    public static List<Completers.TreeCompleter.Node> nodes = new ArrayList<>();

    public static Completer completer;
    public static void init() {

        try {

            completer = new Completers.TreeCompleter(
                    nodes);

            if(!Config.isWindows()){
                String[] undefSEGKILL = {"/bin/sh","-c","stty intr undef </dev/tty"};
                Runtime.getRuntime().exec(undefSEGKILL).waitFor();
            }



            terminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .jansi(true)
                    .dumb(true)
                    .build();




            sReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    /*.completer(new MyCompleter())
                    .highlighter(new MyHighlighter())
                    .parser(new MyParser())*/
                    .build();

            sReader.unsetOpt(LineReader.Option.INSERT_TAB);;
            new DebugMod(sReader).debugWidget();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void newReader(){
        sReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                /*.completer(new MyCompleter())
                .highlighter(new MyHighlighter())
                .parser(new MyParser())*/
                .build();
    }

    public static void reloadCompleter(){
        Completers.TreeCompleter completer = new Completers.TreeCompleter(
                nodes);
        LineReaderImpl reader = (LineReaderImpl) sReader;
        reader.setCompleter(completer);
    }

    public BufferedWriter writer;
}
