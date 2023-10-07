package be.alexandre01.dreamnetwork.core.service.screen.stream.external;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenOutWriter;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenStream;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.PrintStream;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 19:38
*/
public class ExternalScreenStream implements IScreenStream {

    @Override
    public void init(String name, IScreen screen) {

    }

    @Override
    public IScreen getScreen() {
        return null;
    }

    @Override
    public PrintStream getOldOut() {
        return null;
    }

    @Override
    public InputStream getOldIn() {
        return null;
    }

    @Override
    public InputStream getReader() {
        return null;
    }

    @Override
    public BufferedWriter getWriter() {
        return null;
    }

    @Override
    public ScreenInput getIn() {
        return null;
    }

    @Override
    public PrintStream getOut() {
        return null;
    }

    @Override
    public boolean isInit() {
        return false;
    }

    @Override
    public Console getConsole() {
        return null;
    }

    @Override
    public IScreenInReader getScreenInReader() {
        return null;
    }

    @Override
    public IScreenOutWriter getScreenOutWriter() {
        return null;
    }
}
