package be.alexandre01.dreamnetwork.client.service.screen.commands;



import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;

public class ScreenExit implements Command.CommandExecutor {
    Screen screen;
    public ScreenExit(Screen screen){
        this.screen = screen;
    }

    @Override
    public boolean execute(String[] args) {
        //   Console.debugPrint("Exit >>"+ Arrays.asList(args));
        if(args[0].equalsIgnoreCase(":exit")){
            Console.setActualConsole("m:default");

            screen.getScreenStream().exit();
            return true;
        }
        return false;
    }
}
