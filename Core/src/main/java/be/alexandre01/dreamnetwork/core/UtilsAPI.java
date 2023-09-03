package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleManager;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.config.ConfigManager;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.console.formatter.Formatter;

import java.util.logging.FileHandler;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 19:32
*/
public class UtilsAPI extends DNUtils {
    static {
        new UtilsAPI();
    }
    ConfigManager configManager = new ConfigManager();
    IConsoleManager consoleManager = new IConsoleManager() {
        @Override
        public Formatter getFormatter() {
            return Core.getInstance().formatter;
        }

        @Override
        public Console getConsole(String name) {
            return Console.getConsole(name);
        }

        @Override
        public FileHandler getFileHandler() {
            return Core.getInstance().getFileHandler();
        }

        @Override
        public IConsoleReader getConsoleReader() {
            return Main.getConsoleReader();
        }
    };
    @Override
    public AccessibilityMenu createAccessibilityMenu() {
        return new CoreAccessibilityMenu();
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public IConsoleManager getConsoleManager(){
        return consoleManager;
    }
}
