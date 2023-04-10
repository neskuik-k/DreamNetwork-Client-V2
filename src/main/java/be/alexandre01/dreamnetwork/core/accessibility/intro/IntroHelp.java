package be.alexandre01.dreamnetwork.core.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;

public class IntroHelp extends IntroductionConsole {

    public IntroHelp() {
        super("help");

        NodeBuilder help = new NodeBuilder(NodeBuilder.create("help"),console);
    }

    @Override
    public void run() {
        console.setConsoleAction(new Console.IConsole() {
            boolean isHelp = false;
            @Override
            public void listener(String[] args) {
                clear();


                if(args[0].equalsIgnoreCase("help")){
                    Main.getCommandReader().getCommands().getCommandsManager().check(new String[]{"help"});
                    Console.debugPrint(Colors.GREEN_BOLD+"TADAAA !");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    new IntroServers();
                    Console.setActualConsole("m:introservers",true);


                }
            }

            @Override
            public void consoleChange() {
                console.setWriting(LanguageManager.getMessage("introduction.help.useCommand"));
                ConsoleReader.sReader.runMacro("help");
                clear();
            }
        });
    }


}
