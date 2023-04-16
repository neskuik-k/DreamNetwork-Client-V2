package be.alexandre01.dreamnetwork.core.console.widgets;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.utils.InfoCmp;

public class BlockMod extends org.jline.widget.Widgets {

        public BlockMod(LineReader reader) {
            super(reader);
            addWidget("enter-widget", this::debugWidget);
            getKeyMap().bind(new Reference("enter-widget"),KeyMap.key(reader.getTerminal(),InfoCmp.Capability.key_enter));
        }

        public boolean debugWidget() {
            try {
                String name = buffer().toString().split("\\s+")[0];
                reader.callWidget(name);
                reader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                reader.getTerminal().writer().println("World!");
                reader.callWidget(LineReader.REDRAW_LINE);
                reader.callWidget(LineReader.REDISPLAY);
                reader.getTerminal().writer().flush();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
