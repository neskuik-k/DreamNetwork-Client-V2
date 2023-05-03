package be.alexandre01.dreamnetwork.core.console;

import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.widgets.BlockMod;
import be.alexandre01.dreamnetwork.core.console.widgets.DebugMod;
import org.jline.builtins.Completers;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;
import org.jline.widget.AutosuggestionWidgets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConsoleReader {
    public static LineReaderImpl sReader;
    public static Terminal terminal;
    public static List<Completers.TreeCompleter.Node> nodes = new ArrayList<>();
    private static AutosuggestionWidgets autosuggestionWidgets;
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




            sReader = (LineReaderImpl) LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    /*.completer(new MyCompleter())
                    .highlighter(new MyHighlighter())
                    .parser(new MyParser())*/
                    .build();


            sReader.unsetOpt(LineReader.Option.INSERT_TAB);
            sReader.setHighlighter(new ConsoleHighlighter());
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

    public static void newReader(){
        sReader = (LineReaderImpl) LineReaderBuilder.builder()
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

    public static void setAutosuggestionWidgets(boolean bool){
        if(bool){
            autosuggestionWidgets.enable();
        }else{
            autosuggestionWidgets.disable();
        }
    }

    public BufferedWriter writer;
}
