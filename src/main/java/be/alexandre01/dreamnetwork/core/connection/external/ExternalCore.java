package be.alexandre01.dreamnetwork.core.connection.external;


import java.util.logging.Level;

import be.alexandre01.dreamnetwork.core.connection.external.handler.ExternalClientHandler;
import be.alexandre01.dreamnetwork.core.connection.external.requests.ExtRequestManager;
import be.alexandre01.dreamnetwork.core.connection.request.ClientRequestManager;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExternalCore {
    @Getter private static final ExternalCore instance = new ExternalCore();

    ExtRequestManager requestManager;
    ExternalClient client;
    ExternalClientHandler clientHandler;
    Console console;
    boolean isInit = false;
    boolean isConnected = false;
    private String ip;
    public ExternalCore(){

    }

    public void initialize(String ip){
        this.ip = ip;
        if(isInit){
            return;
        }
        client = new ExternalClient(ip);
        Thread thread = new Thread(client);
        thread.start();

        ExternalConsole console = new ExternalConsole();
        this.console = console.getConsole();



    }
    public void sendMessage(String message, Level level){
        console.fPrint(message,level);
    }

    public void sendMessage(String message){
        sendMessage(message,Level.INFO);
    }

    public void writeAndFlush(Message message){
        clientHandler.writeAndFlush(message);
    }

    public void exitMode(){
        System.out.println("Exited external mode");
        isInit = false;
    }


    public void init() {

    }
}
