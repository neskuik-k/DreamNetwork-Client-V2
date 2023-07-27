package be.alexandre01.dreamnetwork.core.service.screen.stream;


import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenOutWriter;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenStream;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleMessage;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.core.service.screen.stream.patches.bungee.BungeeCordReader;
import be.alexandre01.dreamnetwork.core.service.screen.stream.patches.spigot.SpigotReader;
import lombok.Getter;
import org.jline.reader.LineReader;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class ScreenStream implements IScreenStream {
    public IScreen screen;
    public PrintStream oldOut = System.out;
    public InputStream oldIn = System.in;
    public InputStream reader;
    public BufferedWriter writer;
    public ScreenInput in;
    public PrintStream out;


    public boolean isInit;
    Console console;
    IScreenInReader screenInReader;
    IScreenOutWriter screenOutWriter;
    public ScreenStream(String name,Screen screen){
        Console.load("s:"+name);
        this.console = Console.getConsole("s:"+name);
        if(Main.getGlobalSettings().isScreenNameInConsoleChange()){
            String[] s = name.split("/");
            String oneToLast = Arrays.stream(s).skip(1).reduce((first, second) -> second+"/").orElse(null);
            String value = Colors.ANSI_CYAN+s[0]+Colors.YELLOW_BOLD+"/"+Colors.WHITE_BRIGHT+oneToLast;
            char mask = Config.isWindows() ? (char)'*' : (char) '⬩';
            console.setWriting(mask+" "+value+Colors.ANSI_CYAN+" > ");
        }
        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public boolean onKill(LineReader reader) {
                Console.setActualConsole("m:default");
                return true;
            }
        });
        this.screen = screen;
        reader = new BufferedInputStream(screen.getService().getProcess().getInputStream());

        // read outputstream entry

        //System.out.println(screen.getService().getProcess().getOutputStream().getClass());
        //System.out.println(screen.getService().getProcess().getOutputStream().toString());
          //new BufferedOutputStream(screen.getService().getProcess().getOutputStream());
        screenInReader = new ScreenInReader(console,screen.getService(),reader,screen);
        ExecType execType = screen.getService().getJvmExecutor().getExecType();
        if(execType != null){
            if(execType == ExecType.BUNGEECORD){
                screenInReader.getReaderLines().add(new BungeeCordReader());
            }


            if(execType == ExecType.SPIGOT){
               screenInReader.getReaderLines().add(new SpigotReader());
            }
        }

        Thread screenIRT = new Thread(screenInReader);
        screenIRT.start();
    }
    @Override
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

            this.screenOutWriter = new ScreenOutWriter(screen,console);
        }
        ((ScreenOutWriter)screenOutWriter).run();
        Console.setActualConsole("s:"+name);
        ArrayList<ConsoleMessage> h = Console.getCurrent().getHistory();
        if(!h.get(h.size()-1).content.endsWith("\n")){
            console.defaultPrint.print("\n");
        }
    }

}
