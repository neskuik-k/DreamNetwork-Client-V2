package be.alexandre01.dreamnetwork.client.commands;


import be.alexandre01.dreamnetwork.client.commands.lists.HelpCommand;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.ConsoleReader;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import jline.console.completer.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;


public class Command{
    private HashMap<String, SubCommandExecutor> c;
    @Getter private String name;
    @Getter private HelpCommand.HelpBuilder helpBuilder;

    protected CommandExecutor commandExecutor = new CommandExecutor() {
        @Override
        public boolean execute(String[] args) {
            try {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if(subArgs.length < 1){
                    sendHelp();
                    return true;
                }
                if(!c.containsKey(subArgs[0])){
                    sendHelp();
                    return true;
                }
                if(!c.get(subArgs[0]).onSubCommand(subArgs)){
                    sendHelp();
                }
                return true;
            }catch (Exception e){
                return true;
            }
        }
    };
    public void setAutoCompletions(){
        jline.console.ConsoleReader reader = ConsoleReader.sReader;
        c.forEach((s, subCommandExecutor) -> {
            if(subCommandExecutor instanceof SubCommandCompletor){
                SubCommandCompletor completor = (SubCommandCompletor) subCommandExecutor;

                for(String[] subs: completor.sub){
                    Console.print(Arrays.toString(subs), Level.FINE);
                    List<Completer> completors = new ArrayList<>();
                    boolean nullComp = false;
                    for(String sub : subs){
                        completors.add(new StringsCompleter(sub));
                    }



                   // AggregateCompleter aggregateCompleter = new AggregateCompleter(completors);
                    CustomArgumentCompleter argumentCompleter = new CustomArgumentCompleter(completors);


                        argumentCompleter.setStrict(true);
                    reader.addCompleter(argumentCompleter);
                }
            }
        });
    }
    public void setCompletions(Completer completer){
        ConsoleReader.sReader.addCompleter(completer);
    }
    public void sendHelp(){
        if(helpBuilder.getSize() <= 1){
            Console.print(Colors.ANSI_RED()+" Invalid arguments.");
        }else {
            helpBuilder.build();
        }
    }
    public Command(String name){
        this.name = name;
        c = new HashMap<>();
        helpBuilder = new HelpCommand.HelpBuilder(name);
    }
    public void addSubCommand(String subCommand, SubCommandExecutor sce){
        c.put(subCommand,sce);
    }

    public SubCommandExecutor getSubCommand(String subCommand){
        return c.get(subCommand);
    }

    public boolean onCommand(String[] args){
       if(commandExecutor.execute(args))
           return true;
       return false;
    }

    public interface CommandExecutor{
        boolean execute(String[] args);
    }


}

