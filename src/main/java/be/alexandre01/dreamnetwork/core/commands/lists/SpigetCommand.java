package be.alexandre01.dreamnetwork.core.commands.lists;


import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;


import static be.alexandre01.dreamnetwork.core.console.jline.completors.CustomTreeCompleter.node;

public class SpigetCommand extends Command {

    public SpigetCommand(String name) {
        super(name);
        NodeBuilder nodeBuilder = new NodeBuilder(NodeBuilder.create(NodeBuilder.of("spiget",getBaseColor()+"spiget "+ Console.getEmoji("potable_water"))));
        //setCompletion(node("spiget"));
        //setCompletions(new StringsCompleter("spiget"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                Console.setActualConsole("m:spiget");
                return true;
            }
        };
    }

    @Override
    public String getBaseColor() {
        return Colors.YELLOW_BOLD_BRIGHT;
    }

    @Override
    public String getEmoji() {
        return Console.getEmoji("potable_water");
    }


}
