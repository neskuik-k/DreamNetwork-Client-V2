package be.alexandre01.dreamnetwork.core.service.screen.stream.external;


import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenOutWriter;
import be.alexandre01.dreamnetwork.core.Core;

import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualService;
import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenCommands;
import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenExit;
import be.alexandre01.dreamnetwork.core.service.screen.stream.internal.ProcessScreenStream;


import java.io.*;


public class ExternalScreenOutWriter implements IScreenOutWriter {
    ScreenCommands commands;
    private final IScreen screen;
    private final Console console;
    //private final ConsoleReader consoleReader;

    public ExternalScreenOutWriter(IScreen screen, Console console, ProcessScreenStream screenStream) {
        this.console = console;
        this.screen = screen;
        commands = new ScreenCommands(screen);
        commands.addCommands(new ScreenExit(screen,screenStream));
    }

    public void run() {

        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
                //   Console.debugPrint(String.valueOf(args.length));
                if (args.length != 0) {
                    //Console.debugPrint("capte");
                    boolean hasFound = false;
                    if (!args[0].equalsIgnoreCase(" ")) {
                        //Console.debugPrint(Arrays.toString(args));
                        if (!commands.check(args)) {
                            try {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] != null) {
                                        sb.append(args[i]);
                                    }
                                    if (args.length - 1 != i) {
                                        sb.append(" ");
                                    }

                                }
                                writeOnConsole(sb.toString());
                            } catch (IOException e) {
                                e.printStackTrace(Core.getInstance().formatter.getDefaultStream());
                            }
                        }
                    }
                }
            }

            @Override
            public void consoleChange() {
                // do nothing
            }
        });
    }

    @Override
    public void writeOnConsole(String data) throws IOException {
        if(screen.getService() instanceof VirtualService){
            VirtualService virtualService = (VirtualService) screen.getService();
            ExternalClient externalExecutor = virtualService.getJvmExecutor().getExternalCore();
            externalExecutor.getRequestManager().getRequest(RequestType.DEV_TOOLS_SEND_COMMAND, virtualService.getTrueFullName(),data).dispatch();
        }
    }

}
