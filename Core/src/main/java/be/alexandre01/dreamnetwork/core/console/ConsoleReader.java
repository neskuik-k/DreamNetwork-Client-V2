package be.alexandre01.dreamnetwork.core.console;


import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleHighlighter;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.core.console.widgets.DebugMod;
import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutosuggestionWidgets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ConsoleReader implements IConsoleReader {
    public LineReaderImpl sReader;
    public Terminal terminal;
    public List<Completers.TreeCompleter.Node> nodes = new ArrayList<>();
    private AutosuggestionWidgets autosuggestionWidgets;
    @Getter
    private IConsoleHighlighter defaultHighlighter;
    public Completer completer;


    public BufferedWriter writer;

    public void init() {

        try {

            completer = new Completers.TreeCompleter(nodes);

            if (!Config.isWindows()) {
                String[] undefSEGKILL = {"/bin/sh", "-c", "stty intr undef </dev/tty"};
                Runtime.getRuntime().exec(undefSEGKILL).waitFor();
            }


            terminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .jansi(true)
                    //.jna(true)
                    .dumb(true)
                    .build();


            sReader = (LineReaderImpl) LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    /*.completer(new MyCompleter())
                    .highlighter(new MyHighlighter())
                    .parser(new MyParser())*/
                    .build();


            sReader.unsetOpt(LineReader.Option.INSERT_TAB);

            // Create autosuggestion widgets
            autosuggestionWidgets = new AutosuggestionWidgets(sReader);
// Enable autosuggestions
            autosuggestionWidgets.disable();
            /*Widget readAllKeysWidget = new Widget() {
                public boolean apply() {
                    BindingReader bindingReader = new BindingReader(sReader);
                    while (true) {
                        int c = bindingReader.readCharacter();
                        if (c == -1) {
                            break;
                        }
                        System.out.println("Key pressed: " + (char) c);
                    }
                    return true;
                }
            };
            sReader.getWidgets().put("read-all-keys-widget", readAllKeysWidget);

            KeyMap<Binding> keyMap = sReader.getKeyMaps().get("main");*/
            // get all keys
            //get CharSequence of all character possible
            //


            //keyMap.bind((Binding) new Reference("read-all-keys-widget"), allChars);
            /*  eader.getKeyMaps().get("main");
            keyMap.bind(Binding.readChar(), "\u0000");


            while (true) {
                try {
                    int c = sReader.readBinding(Operation.READ_CHAR);
                    // process the character here
                    System.out.println("You typed: " + (char) c);
                } catch (EndOfFileException e) {
                    break;
                }
            }*/
            new DebugMod(sReader).debugWidget();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void initHighlighter() {
        defaultHighlighter = new ConsoleHighlighter();
        sReader.setHighlighter(defaultHighlighter);
    }

    public void newReader() {
        sReader = (LineReaderImpl) LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                /*.completer(new MyCompleter())
                .highlighter(new MyHighlighter())
                .parser(new MyParser())*/
                .build();
    }

    @Override
    public void setNodes(List<Completers.TreeCompleter.Node> list) {
        this.nodes = list;
    }

    public void reloadCompleter() {
        Completers.TreeCompleter completer = new Completers.TreeCompleter(nodes);

        LineReaderImpl reader = (LineReaderImpl) sReader;
        reader.setCompleter(completer);
    }

    public void setAutosuggestionWidgets(boolean bool) {
        if (bool) {
            autosuggestionWidgets.enable();
        } else {
            autosuggestionWidgets.disable();
        }
    }
}
