package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.commands.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;


import java.util.logging.Level;

public class Screen implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(String[] args) {
        if(!args[0].equalsIgnoreCase("screen")){
            return false;
        }else {
            System.out.println("ok");
        }

        if(args.length == 1){
            notComplete();
            return true;
        }
        System.out.println("C'est ->" + ScreenManager.instance.getScreens());

        if(ScreenManager.instance.containsScreen(args[1])){
            ScreenManager.instance.watch(args[1]);
            System.out.println("Connected to screen");
        }else {
            Console.print("There is no screen",Level.ALL);
        }
        return true;
    }

    private void notComplete(){
        Console.print(Colors.RED+"screen servername", Level.ALL);
    }
}
