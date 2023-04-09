package be.alexandre01.dreamnetwork.core.connection.request.exception;

import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;

import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo){
        super(LanguageManager.getMessage("connection.request.exception.requestNotFound").replaceFirst("%var%", requestInfo.name()));
        if(Ansi.isEnabled()){
            Core.getLogger().severe(LanguageManager.getMessage("connection.request.exception.errorCause").replaceFirst("%var%", getMessage()).replaceFirst("%var%", getClass().getSimpleName()));
            for(StackTraceElement s : getStackTrace()){
                Core.getInstance().formatter.getDefaultStream().println("----->");
                Core.getLogger().severe(LanguageManager.getMessage("connection.request.exception.errorOn").replaceFirst("%var%", s.getClassName()).replaceFirst("%var%", s.getMethodName()).replaceFirst("%var%", String.valueOf(s.getLineNumber())));
            }
            return;
        }
        Core.getLogger().severe(LanguageManager.getMessage("connection.request.exception.errorCause").replaceFirst("%var%", getMessage()).replaceFirst("%var%", getClass().getSimpleName()));
        for(StackTraceElement s : getStackTrace()){
            Core.getInstance().formatter.getDefaultStream().println("----->");
            Core.getLogger().severe(LanguageManager.getMessage("connection.request.exception.errorOn").replaceFirst("%var%", s.getClassName()).replaceFirst("%var%", s.getMethodName()).replaceFirst("%var%", String.valueOf(s.getLineNumber())));
        }

    }


}
