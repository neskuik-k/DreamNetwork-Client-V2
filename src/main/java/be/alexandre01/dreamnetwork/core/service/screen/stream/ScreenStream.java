package be.alexandre01.dreamnetwork.core.service.screen.stream;


import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import org.jline.reader.LineReader;


import java.io.*;

public class ScreenStream {
    public IScreen screen;
    public PrintStream oldOut = System.out;
    public InputStream oldIn = System.in;
    public InputStream reader;
    public BufferedWriter writer;
    public ScreenInput in;
    public PrintStream out;
    public boolean isInit;
    Console console;
    ScreenInReader screenInReader;
    ScreenOutReader screenOutReader;
    public ScreenStream(String name,Screen screen){
        Console.load("s:"+name);
        this.console = Console.getConsole("s:"+name);
        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public void onKill(LineReader reader) {
                Console.setActualConsole("m:default");
                screen.getScreenStream().exit();
                Console.getConsole("m:default").run();
            }
        });
        this.screen = screen;
        reader = new BufferedInputStream(screen.getService().getProcess().getInputStream());
        screenInReader = new ScreenInReader(console,screen.getService(),reader,screen);
        Thread screenIRT = new Thread(screenInReader);
        screenIRT.start();
    }
    public void init(String name, IScreen screen){
        this.screen = screen;
     //   reader = new BufferedInputStream(screen.getService().getProcess().getInputStream());

        //reader = new BufferedReader(new InputStreamReader(screen.getService().getProcess().getInputStream()));


        LineReader c = null;
       if(console.getConsoleAction() == null){
           /*  try {
                c = LineReaderBuilder.builder()
                        .terminal(terminal)
                        :completer(new MyCompleter())
                        .highlighter(new MyHighlighter())
                        .parser(new MyParser())
                        .build();
                c = new LineReaderBuilder.builder().terminal()(screen.getService().getProcess().getInputStream(), screen.getService().getProcess().getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            this.screenOutReader = new ScreenOutReader(screen,console);
        }
        screenOutReader.run();
        Console.setActualConsole("s:"+name);
    }

    public void exit(){
     //   console.destroy();

        //screenOutReader.stop();
        //screenOutReader.interrupt();
      /*  screenInReader.isRunning = false;

        screenInReader.stop();
        screenInReader.interrupt();*/

  /*      isInit = false;
        Console.clearConsole();
        System.setIn(oldIn);
        System.setOut(oldOut);*/
    }
}
