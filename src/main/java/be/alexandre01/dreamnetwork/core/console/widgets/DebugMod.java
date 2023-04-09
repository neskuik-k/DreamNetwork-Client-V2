package be.alexandre01.dreamnetwork.core.console.widgets;

import static org.jline.keymap.KeyMap.ctrl;
    import static org.jline.keymap.KeyMap.alt;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
    import org.jline.reader.Reference;
import org.jline.utils.InfoCmp;

public class DebugMod extends org.jline.widget.Widgets {
        
        public DebugMod(LineReader reader) {
            super(reader);
            addWidget("debug-widget", this::debugWidget);
            getKeyMap().bind(new Reference("debug-widget"),KeyMap.key(reader.getTerminal(),InfoCmp.Capability.key_f9));
        }

        public boolean debugWidget() {
            try {
                String name = buffer().toString().split("\\s+")[0];
                reader.callWidget(name);
                boolean debug = Main.getInstance().isDebug();
                Main.getInstance().setDebug(!debug);
                Console.getConsoles().forEach(console -> {
                    console.isDebug = !debug;
                });

                Console.clearConsole();

                if(!debug)
                    System.out.println(LanguageManager.getMessage("debug.enabled"));
                else
                    System.out.println(LanguageManager.getMessage("debug.disabled"));

                Console.fine("This is a debug message !");
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
