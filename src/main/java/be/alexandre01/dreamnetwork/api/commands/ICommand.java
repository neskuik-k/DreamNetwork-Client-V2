package be.alexandre01.dreamnetwork.api.commands;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.jline.completors.CustomTreeCompleter;
import lombok.Getter;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;

import java.util.HashMap;

public abstract class ICommand {
    protected final HashMap<String, SubCommandExecutor> subCommands;
    @Getter
    public String name;
    @Getter public HelpBuilder helpBuilder;
    public ICommand(String name){
        this.name = name;
        subCommands = new HashMap<>();
        helpBuilder = new HelpBuilder(name);
    }
   abstract public void setCompletion(CustomTreeCompleter.Node node);

   abstract public void setAutoCompletions();

   abstract public void setCompletionsTest(Completer completer);

   abstract public void sendHelp();

    public String getBaseColor() {
        return Colors.WHITE_BOLD_BRIGHT;
    }
    public String getEmoji() {
        return "";
    }

    abstract public void addSubCommand(String subCommand, SubCommandExecutor sce);

   abstract public SubCommandExecutor getSubCommand(String subCommand);

   public abstract boolean onCommand(String[] args);

   abstract public String getName();

   abstract public HelpBuilder getHelpBuilder();

    public interface CommandExecutor {
        boolean execute(String[] args);
    }
}
