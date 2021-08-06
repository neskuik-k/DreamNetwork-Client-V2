package be.alexandre01.dreamnetwork.client.console;

import java.util.logging.Level;

public class ConsoleMessage {
    public String content;
    public Level level;

    public ConsoleMessage(String content, Level level) {
        this.content = content;
        this.level = level;
    }
    public ConsoleMessage(String content) {
        this.content = content;
        this.level = null;
    }
}
