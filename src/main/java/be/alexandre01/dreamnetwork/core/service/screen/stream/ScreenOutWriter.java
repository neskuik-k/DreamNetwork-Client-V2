package be.alexandre01.dreamnetwork.core.service.screen.stream;


import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;

import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenCommands;
import be.alexandre01.dreamnetwork.core.service.screen.commands.ScreenExit;


import java.io.*;

public class ScreenOutWriter {
    ScreenCommands commands;
    BufferedWriter writer;
    private String[] args;
    private final IScreen screen;
    private final Console console;
    //private final ConsoleReader consoleReader;

    public ScreenOutWriter(IScreen screen, Console console){
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
        commands.addCommands(new ScreenExit(screen));
    }

    public void run() {

        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {
                //   Console.debugPrint(String.valueOf(args.length));
                System.out.println("Console screen writing");
                if (args.length != 0) {
                    //Console.debugPrint("capte");
                    boolean hasFound = false;
                    if (!args[0].equalsIgnoreCase(" ")) {
                        //Console.debugPrint(Arrays.toString(args));
                        if (!commands.check(args)) {
                            try {
                                if(!screen.getService().getProcess().isAlive()){
                                    screen.destroy();
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

                                Writer writer = new OutputStreamWriter(screen.getService().getProcess().getOutputStream());
                                writer.write(sb.toString()+"\n");

                                //  Console.debugPrint("write");
                                writer.flush();
                                // Console.debugPrint("flush");
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



//            write("> ");
         /*   try {
                args = reader.readLine().split(" ");
            }catch (Exception e){

            }*/
        }

}
