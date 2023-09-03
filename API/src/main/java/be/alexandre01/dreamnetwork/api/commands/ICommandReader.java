package be.alexandre01.dreamnetwork.api.commands;

import be.alexandre01.dreamnetwork.api.console.Console;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 18:21
*/
public interface ICommandReader {
    static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }

    void write(String str);

    ICommandsManager getCommands();

    Console getConsole();
}
