package be.alexandre01.dreamnetwork.core.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;

public class IntroHelp extends IntroductionConsole {

    public IntroHelp() {
        super("help");

        NodeBuilder help = new NodeBuilder(NodeBuilder.create("yes"),console);
    }

    @Override
    public void run() {
        console.setConsoleAction(new Console.IConsole() {
            boolean isHelp = false;
            @Override
            public void listener(String[] args) {
                Console.clearConsole();


                if(args[0].equalsIgnoreCase("help")){
                    Console.setActualConsole("m:default");
                    Main.getCommandReader().getCommands().getCommandsManager().check(new String[]{"help"});
                   /* try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*/


                }
            }

            @Override
            public void consoleChange() {
                console.setWriting(Colors.GREEN+"You can use the command "+Colors.CYAN+"help"+Colors.GREEN+" to get help"+Colors.WHITE+" : "+Colors.RED);
                ConsoleReader.sReader.runMacro("help");
            }
        });
    }
}
