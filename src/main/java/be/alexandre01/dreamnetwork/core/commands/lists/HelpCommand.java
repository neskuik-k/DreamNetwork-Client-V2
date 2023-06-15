package be.alexandre01.dreamnetwork.core.commands.lists;


import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.CommandReader;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);
        NodeBuilder nodeBuilder = new NodeBuilder(NodeBuilder.create(NodeBuilder.of("help",getBaseColor()+"help "+ Console.getEmoji("thinking"))));
        //setCompletion(node("help"));
        //setCompletions(new StringsCompleter("help"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                //String name = Console.getFromLang("name");
                //String server = Console.getFromLang("server");

                CommandReader cr = Main.getCommandReader();
                cr.getCommands().getCommandsManager().executorList.forEach((cmd, iCommand) -> {
                    if(iCommand.getHelpBuilder().getSize() > 1){
                        iCommand.getHelpBuilder().build();
                    }
                });

                /*Console.printLang("commands.help.list");
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                Console.printLang("commands.help.howTo.manageService");
                Console.print("service:");
                Console.print("service add server [" + name + "] | " + Console.getFromLang("commands.help.addA", server));
                Console.print("service add proxy [" + name + "] | " + Console.getFromLang("commands.help.addA", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
                Console.printLang("commands.help.howTo.startStopService");
                Console.print("service start server [" + name + "] | " + Console.getFromLang("commands.help.startA", server));
                Console.print("service start proxy [" + name + "] | " + Console.getFromLang("commands.help.startA", "proxy"));
                Console.debugPrint("");
                Console.print("service stop server [" + name + "] | " + Console.getFromLang("commands.help.stopA", server));
                Console.print("service stop proxy [" + name + "] | " + Console.getFromLang("commands.help.stopA", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.printLang("commands.help.howTo.connectConsole");
                Console.print("service screen server [" + name + "] | " + Console.getFromLang("commands.help.connectTo", server));
                Console.print("service screen proxy [" + name + "] | " + Console.getFromLang("commands.help.connectTo", "proxy"));

                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.printLang("commands.help.howTo.removeService");
                Console.print("service remove server [" + name + "] | " + Console.getFromLang("commands.help.removeA", server));
                Console.print("service remove proxy [" + name + "] | " + Console.getFromLang("commands.help.removeA", "proxy"));
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());*/
                return true;
            }
        };
    }
    @Override
    public String getEmoji() {
        return Console.getEmoji("thinking");
    }

}
