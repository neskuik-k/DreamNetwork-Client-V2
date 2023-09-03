package be.alexandre01.dreamnetwork.core.console.widgets;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.utils.InfoCmp;

import java.util.ArrayList;
import java.util.List;

public class DebugMod extends org.jline.widget.Widgets {
        
        public DebugMod(LineReader reader) {
            super(reader);
            List<String> strings = new ArrayList<>();

            addWidget("debug-widget", this::debugWidget);
            getKeyMap().bind(new Reference("debug-widget"),KeyMap.key(reader.getTerminal(),InfoCmp.Capability.key_f9));
            //all chars on List with special chars

            //add all utf-8 chars
            //getKeyMap().bind(new Reference("debug-widget"), strings);
        }

        public boolean debugWidget() {
            try {
                String name = buffer().toString().split("\\s+")[0];
                reader.callWidget(name);

                Core c = Core.getInstance();
                boolean debug = c.isDebug();
                c.setDebug(!debug);
                Console.getConsoles().forEach(console -> {
                    console.isDebug = !debug;
                });

                Console.clearConsole();

                if(!debug)
                    Console.printLang("debug.enabled");
                else
                    Console.printLang("debug.disabled");

                Console.fine("This is a debug message !");
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
