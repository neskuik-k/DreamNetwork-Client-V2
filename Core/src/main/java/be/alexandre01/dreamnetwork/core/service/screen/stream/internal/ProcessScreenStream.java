package be.alexandre01.dreamnetwork.core.service.screen.stream.internal;


import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenOutWriter;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenStream;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualService;
import be.alexandre01.dreamnetwork.core.service.JVMService;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import be.alexandre01.dreamnetwork.core.service.screen.stream.external.ExternalScreenInReader;
import be.alexandre01.dreamnetwork.core.service.screen.stream.external.ExternalScreenOutWriter;
import be.alexandre01.dreamnetwork.core.service.screen.stream.patches.bungee.BungeeCordReader;
import be.alexandre01.dreamnetwork.core.service.screen.stream.patches.spigot.SpigotReader;
import lombok.Getter;
import org.jline.reader.LineReader;


import java.io.*;
import java.util.Arrays;

@Getter
public class ProcessScreenStream implements IScreenStream {
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
    public ProcessScreenStream(String name, Screen screen){
        Console.load("s:"+name);
        this.console = Console.getConsole("s:"+name);
        if(Main.getGlobalSettings().isScreenNameInConsoleChange()){
            String value = Colors.ANSI_CYAN+name;
            if(name.contains("/")){
                String[] s = name.split("/");
                String oneToLast = Arrays.stream(s).skip(1).reduce((first, second) -> second+"/").orElse(null);
                value = Colors.ANSI_CYAN+s[0]+Colors.YELLOW_BOLD+"/"+Colors.WHITE_BRIGHT+oneToLast;
            }
            char mask = Config.isWindows() ? (char)'*' : (char) '⬩';
            console.setWriting(mask+" "+value+Colors.ANSI_CYAN+" > "+Colors.RESET);
        }
        console.setKillListener(new Console.ConsoleKillListener() {
            @Override
            public boolean onKill(LineReader reader) {
                exitScreen();
                return true;
            }
        });
        this.screen = screen;


        // read outputstream entry

        //System.out.println(screen.getService().getProcess().getOutputStream().getClass());
        //System.out.println(screen.getService().getProcess().getOutputStream().toString());
          //new BufferedOutputStream(screen.getService().getProcess().getOutputStream());
        if(console.getConsoleAction() == null){
            if(screen.getService() instanceof JVMService){
                System.out.println();
                screenOutWriter = new ProcessScreenOutWriter(screen, console,this);
            }else{
                screenOutWriter = new ExternalScreenOutWriter(screen, console,this);
            }
        }
        if(screen.getService() instanceof JVMService){
            reader = new BufferedInputStream(screen.getService().getProcess().getInputStream());
            screenInReader = new ProcessScreenInReader(console,screen.getService(),reader,screen);
            screen.getService().getExecutor().getExecType().ifPresent(execType -> {
                if(execType == ExecType.BUNGEECORD){
                    screenInReader.getReaderLines().add(new BungeeCordReader());
                }
                if(execType == ExecType.SERVER){
                    screenInReader.getReaderLines().add(new SpigotReader());
                }
            });
            Thread screenIRT = new Thread(screenInReader);
            screenIRT.start();
        }
    }
    @Override
    public void init(String name, IScreen screen){
        this.screen = screen;
     //   reader = new BufferedInputStream(screen.getService().getProcess().getInputStream());

        //reader = new BufferedReader(new InputStreamReader(screen.getService().getProcess().getInputStream()));


        LineReader c = null;


       if(screen.getService() instanceof VirtualService){
           screenInReader = new ExternalScreenInReader(console,screen.getService(),screen);
           screenInReader.run();
       }

       screenOutWriter.run();
        Console.setActualConsole("s:"+name);
       /* ArrayList<ConsoleMessage> h = Console.getCurrent().getHistory();


        if(!h.isEmpty()){
            if(!h.get(h.size()-1).content.endsWith("\n")){
                console.defaultPrint.print("\n");
            }
        }*/
    }

    public void exitScreen(){
        Console.goToPrevious();
        if(screenInReader instanceof ExternalScreenInReader){
            screenInReader.stopReader();
        }
    }


}
