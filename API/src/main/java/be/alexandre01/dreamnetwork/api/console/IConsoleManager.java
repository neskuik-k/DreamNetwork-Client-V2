package be.alexandre01.dreamnetwork.api.console;

import be.alexandre01.dreamnetwork.api.console.formatter.IFormatter;

import java.util.logging.FileHandler;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:48
*/
public interface IConsoleManager {
    public IFormatter getFormatter();

    public Console getConsole(String name);

    public FileHandler getFileHandler();

    public IConsoleReader getConsoleReader();


}
