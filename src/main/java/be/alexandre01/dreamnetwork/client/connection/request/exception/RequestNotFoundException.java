package be.alexandre01.dreamnetwork.client.connection.request.exception;

import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;

import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo){
        super("The request "+requestInfo.name+ " isn't foundable.");
        if(Ansi.isEnabled()){
           Client.getLogger().severe(Ansi.Color.RED+"ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
            for(StackTraceElement s : getStackTrace()){
                Client.getInstance().formatter.getDefaultStream().println("----->");
                Client.getLogger().severe("ERROR ON>> "+ Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET());
            }
            return;
        }

       Client.getLogger().severe("ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
        for(StackTraceElement s : getStackTrace()){
            Client.getInstance().formatter.getDefaultStream().println("----->");
            Client.getLogger().severe("ERROR ON>> "+ s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber());
        }

    }


}
