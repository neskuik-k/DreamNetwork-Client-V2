package be.alexandre01.dreamnetwork.core.service.screen.stream.internal;


import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenOutWriter;
import be.alexandre01.dreamnetwork.core.Core;

import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenCommands;
import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenExit;


import java.io.*;


public class ProcessScreenOutWriter implements IScreenOutWriter {
    ScreenCommands commands;
    BufferedWriter writer;
    private String[] args;
    private final IScreen screen;
    private final Console console;
    //private final ConsoleReader consoleReader;

    public ProcessScreenOutWriter(IScreen screen, Console console, ProcessScreenStream screenStream) {
        //  this.consoleReader = consoleReader;
        //Console.debugPrint(consoleReader.getCompleters());

        this.console = console;
        this.screen = screen;
        commands = new ScreenCommands(screen);
        /*reader = new BufferedReader(new InputStreamReader(screen.getScreenStream().in));
        write("> ");

        try {
            args = reader.readLine().split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
                                if (!screen.getService().getProcess().isAlive()) {
                                    Console.fine("The PROCESS cannot be writed anymore.");
                                    screen.destroy(false);
                                    return;
                                }
                                //   Console.debugPrint("start");

                                //  Console.debugPrint("writer");

                                //writer.write(args[args.length-1]+"\n");
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

            }
        });
    }

    @Override
    public void writeOnConsole(String data) throws IOException {
        //Writer writer = new OutputStreamWriter(screen.getService().getProcess().getOutputStream());
        OutputStream writer = screen.getService().getProcess().getOutputStream();
        writer.write((data + "\n").getBytes());
        writer.flush();
    }

}
