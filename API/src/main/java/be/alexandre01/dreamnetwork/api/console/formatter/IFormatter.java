package be.alexandre01.dreamnetwork.api.console.formatter;

import java.io.PrintStream;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:50
*/
public interface IFormatter {
    PrintStream getDefaultStream();

    PrintStream getPrStr();

    public IConciseFormatter getDefaultFormatter();
}
