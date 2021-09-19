package be.alexandre01.dreamnetwork.client.console;

import org.jline.builtins.Completers;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
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
    public static void init() {

        try {

            Completers.TreeCompleter completer = new Completers.TreeCompleter(
                    nodes);

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .build();




            sReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    /*.completer(new MyCompleter())
                    .highlighter(new MyHighlighter())
                    .parser(new MyParser())*/
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }

      //  sReader.setHandleUserInterrupt(true);



    }

    public BufferedWriter writer;
}
