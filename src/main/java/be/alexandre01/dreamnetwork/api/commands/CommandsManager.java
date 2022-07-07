package be.alexandre01.dreamnetwork.api.commands;



import java.util.HashMap;

public class CommandsManager  {

    private final be.alexandre01.dreamnetwork.client.commands.CommandsManager commandsManager = new be.alexandre01.dreamnetwork.client.commands.CommandsManager();

    public CommandsManager(){
       this.commandsManager.executorList = new HashMap<>();

   }


   public void addCommands(ICommand cmd){
       commandsManager.addCommands(cmd);
   }


   public void check(String[] args){

       commandsManager.check(args);
   }

}
