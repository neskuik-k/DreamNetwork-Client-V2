package be.alexandre01.dreamnetwork.core.service.screen.commands;



import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.console.Console;

public class ScreenExit implements Command.CommandExecutor {
    IScreen screen;
    public ScreenExit(IScreen screen){
        this.screen = screen;
    }

    @Override
    public boolean execute(String[] args) {
        //   Console.debugPrint("Exit >>"+ Arrays.asList(args));
        if(args[0].equalsIgnoreCase(":exit")){
            Console.setActualConsole("m:default");

            return true;
        }
        return false;
    }
}
