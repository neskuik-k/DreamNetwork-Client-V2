package be.alexandre01.dreamnetwork.core.connection.request.exception;

import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.Core;

import be.alexandre01.dreamnetwork.core.console.Console;
import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo){
        super(Console.getFromLang("connection.request.exception.requestNotFound", requestInfo.name()));
        if(Ansi.isEnabled()){
            Core.getLogger().severe(Console.getFromLang("connection.request.exception.errorCause", getMessage(), getClass().getSimpleName()));
            for(StackTraceElement s : getStackTrace()){
                Core.getInstance().formatter.getDefaultStream().println("----->");
                Core.getLogger().severe(Console.getFromLang("connection.request.exception.errorOn", s.getClassName(), s.getMethodName(), s.getLineNumber()));
            }
            return;
        }
        Core.getLogger().severe(Console.getFromLang("connection.request.exception.errorCause", getMessage(), getClass().getSimpleName()));
        for(StackTraceElement s : getStackTrace()){
            Core.getInstance().formatter.getDefaultStream().println("----->");
            Core.getLogger().severe(Console.getFromLang("connection.request.exception.errorOn", s.getClassName(), s.getMethodName(), s.getLineNumber()));
        }

    }


}
