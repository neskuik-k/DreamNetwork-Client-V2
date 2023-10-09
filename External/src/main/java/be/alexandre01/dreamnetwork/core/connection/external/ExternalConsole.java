package be.alexandre01.dreamnetwork.core.connection.external;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.ICommandsManager;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
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

        arguments.add(NodeBuilder.create("screen",new ScreensNode()));
        arguments.add(NodeBuilder.create("stop",new ScreensNode()));
        arguments.add(NodeBuilder.create("start",new BundlesNode(true, true,true)));
        setArgumentsBuilder(arguments.toArray(new NodeContainer[0]));
        List<String> acceptedSub = Arrays.asList("screen", "stop", "start");


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

        console.setKillListener(reader ->  {
            String data;
            while ((data = reader.readLine("Are you sure ? You gonna stop the communication between the main DreamNetwork: ")) != null) {
                if (data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")) {
                    for (IJVMExecutor executor : DNCoreAPI.getInstance().getContainer().getJVMExecutors()) {
                        for (IService service : executor.getServices()){
                            service.stop();
                        }
                    }
                    // stop all servers
                    return true;
                }
                return true;
            }
            return true;
        });
    }

    public void setArgumentsBuilder(NodeContainer... objects){
        for (NodeContainer object : objects) {
            new NodeBuilder(object, console);
        }
    }
}
