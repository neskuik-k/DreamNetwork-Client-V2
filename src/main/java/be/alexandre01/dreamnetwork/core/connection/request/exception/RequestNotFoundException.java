package be.alexandre01.dreamnetwork.core.connection.request.exception;

import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;

import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo){
        super("The request "+requestInfo.name()+ " isn't foundable.");
        if(Ansi.isEnabled()){
           Core.getLogger().severe(Ansi.Color.RED+"ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
            for(StackTraceElement s : getStackTrace()){
                Core.getInstance().formatter.getDefaultStream().println("----->");
                Core.getLogger().severe("ERROR ON>> "+ Colors.WHITE_BACKGROUND+Colors.ANSI_BLACK()+s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber()+Colors.ANSI_RESET());
            }
            return;
        }

       Core.getLogger().severe("ERROR CAUSE>> "+getMessage()+" || "+ getClass().getSimpleName());
        for(StackTraceElement s : getStackTrace()){
            Core.getInstance().formatter.getDefaultStream().println("----->");
            Core.getLogger().severe("ERROR ON>> "+ s.getClassName()+":"+s.getMethodName()+":"+s.getLineNumber());
        }

    }


}
