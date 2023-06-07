package be.alexandre01.dreamnetwork.core.connection.external;

import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import com.google.common.base.Ascii;
import lombok.Getter;

@Getter
public class ExternalConsole  {
    Console console;
    public ExternalConsole() {
        console = Console.load("m:external");
        console.setConsoleAction(new Console.IConsole() {
            @Override
            public void listener(String[] args) {

            }

            @Override
            public void consoleChange() {
                ASCIIART.sendTitle();
                System.out.println("External mode SCREEN");
            }
        });

        console.setKillListener(reader ->  {
            String data;
            while ((data = reader.readLine("Are you sure ? You gonna stop the communication between the main DreamNetwork: ")) != null) {
                if (data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")) {
                    // stop all servers
                    return true;
                }
                return true;
            }
            return true;
        });
    }
}
