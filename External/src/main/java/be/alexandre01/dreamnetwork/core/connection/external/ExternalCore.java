package be.alexandre01.dreamnetwork.core.connection.external;


import java.util.logging.Level;

import be.alexandre01.dreamnetwork.api.connection.external.CoreNetServer;
import be.alexandre01.dreamnetwork.api.connection.external.IExternalCore;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.api.connection.external.handler.IExternalClientHandler;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExternalCore implements IExternalCore {
    @Getter private static final ExternalCore instance = new ExternalCore();

    ExternalServer serverConnection;
    public String connectionID = "N/A";
    IExternalClientHandler clientHandler;
    Console console;
    boolean isInit = false;
    boolean isConnected = false;
    private String ip;
    
    private CoreNetServer server;
    public ExternalCore(){

    }

    public void initialize(String ip){
        this.ip = ip;
        if(isInit){
            return;
        }
        serverConnection = new ExternalServer(ip);
        Thread thread = new Thread(serverConnection);
        thread.start();

        ExternalConsole console = new ExternalConsole();

        this.console = console.getConsole();
        System.out.println("Initialized external mode");
        System.out.println(console.getConsole());
        System.out.println(console.getConsole().getName());
        System.out.println(console.getConsole().getConsoleAction());
        System.out.println(console.getConsole().writing);

        Console.setActualConsole(console.getConsole().getName());
    }
    @Override
    public void sendMessage(String message, Level level){
        console.fPrint(message,level);
    }

    @Override
    public void sendMessage(String message){
        sendMessage(message,Level.INFO);
    }

    @Override
    public void writeAndFlush(Message message){
        clientHandler.writeAndFlush(message);
    }

    @Override
    public void exitMode(){
        if(getClientHandler() != null){
            if(getClientHandler().getChannel() != null && getClientHandler().getChannel().isActive()){
                getClientHandler().getChannel().close();
            }
        }
        System.out.println("Exited external mode");
        Console.setActualConsole("m:default");
        isInit = false;
    }


    @Override
    public void init() {

    }
}
