package be.alexandre01.dreamnetwork.core.connection.request.exception;

import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.Core;

import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.core.console.Console;
import org.fusesource.jansi.Ansi;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo, Client client){
        super(Console.getFromLang("connection.request.exception.requestNotFound", requestInfo.name(),client == null ? "null" : client.getName()));
        Console.bug(this);
    }
    public RequestNotFoundException(RequestInfo requestInfo, String client){
        super(Console.getFromLang("connection.request.exception.requestNotFound", requestInfo.name(),client));
        Console.bug(this);
    }
}
