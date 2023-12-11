package be.alexandre01.dreamnetwork.core.connection.external;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.ICommandsManager;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.ASCIIART;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ExternalConsole  {
    protected List<NodeContainer> arguments =  new ArrayList<>();
    final Console console;
    public ExternalConsole() {
        this.console = Console.load("m:external");

        arguments.add(NodeBuilder.create("screen",NodeBuilder.create(new ScreensNode())));
        arguments.add(NodeBuilder.create("stop",NodeBuilder.create(new ScreensNode())));
        arguments.add(NodeBuilder.create("start",NodeBuilder.create(new BundlesNode(true, true,true))));
        setArgumentsBuilder(arguments.toArray(new NodeContainer[0]));
        List<String> acceptedSub = Arrays.asList("screen", "stop", "start");
        console.setWriting(Colors.YELLOW+"* "+Colors.RED_BRIGHT+"External > "+Colors.RESET);


        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
                ICommandsManager commandsManager = DNCoreAPI.getInstance().getCommandReader().getCommands();
                List<SubCommandExecutor> subCommandExecutor = commandsManager.getExecutorsList().values().stream()
                        .filter(command -> acceptedSub.contains(args[0]))
                        .filter(iCommand -> iCommand.getSubCommand(args[0]) != null)
                        .map(command -> command.getSubCommand(args[0])).collect(Collectors.toList());
                if(!subCommandExecutor.isEmpty()){
                    if(subCommandExecutor.size() == 1){
                        if(subCommandExecutor.get(0).onSubCommand(args)){
                            return;
                        }
                    }
                }
            }

            @Override
            public void consoleChange() {
                ASCIIART.sendTitle();
                System.out.println("External mode SCREEN");
            }
        });
        console.setKillListener(reader -> {
            //Shutdown other things
            console.addOverlay(new Console.Overlay() {
                @Override
                public void on(String data) {
                    disable();
                    if (data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")) {
                        for (IExecutor executor : DNCoreAPI.getInstance().getContainer().getExecutors()) {
                            for (IService service : executor.getServices()){
                                service.stop();
                            }
                            ExternalCore.getInstance().exitMode();
                        }
                        Console.setActualConsole("m:default");
                    }
                }
            }, Colors.PURPLE_BOLD+"The communication will be closed and all servers closed (y/n) ? => ");
            return true;
        });
    }

    public void setArgumentsBuilder(NodeContainer... objects){
        for (NodeContainer object : objects) {
            new NodeBuilder(object, console);
        }
    }
}
