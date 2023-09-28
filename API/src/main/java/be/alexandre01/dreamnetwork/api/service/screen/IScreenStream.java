package be.alexandre01.dreamnetwork.api.service.screen;

import be.alexandre01.dreamnetwork.api.console.Console;

public interface IScreenStream {
    void init(String name, IScreen screen);

    IScreen getScreen();

    java.io.PrintStream getOldOut();

    java.io.InputStream getOldIn();

    java.io.InputStream getReader();

    java.io.BufferedWriter getWriter();

    ScreenInput getIn();

    java.io.PrintStream getOut();

    boolean isInit();

    Console getConsole();

    IScreenInReader getScreenInReader();

    IScreenOutWriter getScreenOutWriter();

    public interface ScreenInput {
        String readLine();
    }
}
