package be.alexandre01.dreamnetwork.client.service.screen.stream;


import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;

import java.io.*;

public class ScreenStream {
    public Screen screen;
    public PrintStream oldOut = System.out;
    public InputStream oldIn = System.in;
    public BufferedReader reader;
    public BufferedWriter writer;
    public ScreenInput in;
    public PrintStream out;
    public boolean isInit;
    Console console;
    ScreenInReader screenInReader;
    ScreenOutReader screenOutReader;
    public ScreenStream(){

    }
    public void init(String name, Screen screen){
        this.screen = screen;
        System.out.println(screen.getService().getProcess().getInputStream());
        if(reader == null)
            reader = new BufferedReader(new InputStreamReader(screen.getService().getProcess().getInputStream()));
        if(writer == null)
            writer = new BufferedWriter(new OutputStreamWriter(screen.getService().getProcess().getOutputStream()));
        System.out.println("init");
        Console.load("s:"+name);
        console = Console.getConsole("s:"+name);

        this.console = console;
        screenInReader = new ScreenInReader(console,screen.getService(),reader,screen);
        Thread screenIRT = new Thread(screenInReader);
        screenIRT.start();
        this.screenOutReader = new ScreenOutReader(screen,console,writer);
        Thread screenORT = new Thread(screenOutReader);
        screenORT.start();

        Console.setActualConsole("s:"+name);
    /*    ByteArrayOutputStream screenOutput = new ByteArrayOutputStream( );
        byte[] bytes = screenOutput.toByteArray();
        in = new ScreenInput(bytes);
        out = new PrintStream(screenOutput);
        System.setIn(in.inputStream);
        System.out.println("in sysout");
        Console.print("in console print");
        try {
            screenOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("out open test");
       Main.getInstance().formatter.getDefaultStream().println("lol");
        System.out.println("out sysout");
        Console.print("out console print");
        isInit = true;*/
    }

    public void exit(){
        console.destroy();

        screenOutReader.stop = true;

        screenOutReader.stop();
        screenOutReader.interrupt();
        screenInReader.isRunning = false;

        screenInReader.stop();
        screenInReader.interrupt();

  /*      isInit = false;
        Console.clearConsole();
        System.setIn(oldIn);
        System.setOut(oldOut);*/
    }
}
