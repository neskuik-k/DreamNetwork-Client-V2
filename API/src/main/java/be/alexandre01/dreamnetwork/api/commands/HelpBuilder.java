package be.alexandre01.dreamnetwork.api.commands;



import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HelpBuilder {
    private ArrayList<Object> sbs;
    private List<Integer> indexLoggerException = new ArrayList<>();
    private String commandName;

    public HelpBuilder(String commandName) {
        this.commandName = commandName;
        sbs = new ArrayList<>();
        sbs.add(Console.getFromLang("api.commands.helpBuilder.list", commandName));
    }

    public HelpBuilder setCmdUsage(String usage, String... sub) {
        StringBuilder sb = new StringBuilder();
        sb.append(Colors.ANSI_PURPLE + commandName + Colors.ANSI_RESET).append(" ");
        for (String s : sub)
            sb.append(s).append(" ");

        sbs.add(sb.toString() + "| " + usage);
        return this;
    }

    public HelpBuilder setTitleUsage(String u) {
        sbs.add(Colors.WHITE_BOLD_UNDERLINED+u);
        return this;
    }

    public HelpBuilder setLines(Colors colors) {
        sbs.add(colors + "   ------------------------------------------------------");
        indexLoggerException.add(sbs.size() - 1);
        return this;
    }

    private Object getLastSB() {
        return sbs.get(sbs.size() - 1);
    }

    public int getSize() {
        return sbs.size();
    }

    public HelpBuilder build() {
        int i = 0;
        for (Object object : sbs) {
            if (indexLoggerException.contains(i)) {
                Console.debugPrint(sbs);
            } else {
                Console.print(object, Level.INFO);
            }
            i++;
        }
        return this;
    }

}
