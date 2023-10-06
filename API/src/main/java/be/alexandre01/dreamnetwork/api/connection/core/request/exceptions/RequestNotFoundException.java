package be.alexandre01.dreamnetwork.api.connection.core.request.exceptions;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;

import be.alexandre01.dreamnetwork.api.console.Console;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException(RequestInfo requestInfo, AServiceClient client){
        super(Console.getFromLang("connection.request.exception.requestNotFound", requestInfo.name(),client == null ? "null" : client.getName()));
        Console.bug(this);
    }
    public RequestNotFoundException(RequestInfo requestInfo, String client){
        super(Console.getFromLang("connection.request.exception.requestNotFound", requestInfo.name(),client));
        Console.bug(this);
    }
}
