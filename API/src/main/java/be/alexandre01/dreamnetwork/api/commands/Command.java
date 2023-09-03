package be.alexandre01.dreamnetwork.api.commands;


import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.console.jline.completors.CustomTreeCompleter;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class Command extends ICommand {

    public Object value;

    public void setCompletorValue(String value,String display){
        this.value = NodeBuilder.of(value,display);
    }

    public void setCompletorValue(String value){
        setCompletorValue(value,value);
    }
    public Object getCompletorValue(){
        return value;
    }
    protected CommandExecutor commandExecutor = new CommandExecutor() {
        @Override
        public boolean execute(String[] args) {
            try {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if(subArgs.length < 1){
                    sendHelp();
                    return true;
                }
                if(!subCommands.containsKey(subArgs[0])){
                    sendHelp();
                    return true;
                }
                if(!subCommands.get(subArgs[0]).onSubCommand(subArgs)){
                    sendHelp();
                }
                return true;
            }catch (Exception e){
                Console.bug(e);
                return true;
            }
        }
    };

    @Override
    public void setCompletion(CustomTreeCompleter.Node node){
        Console.getConsoleReader().getNodes().add(node);
    }
    @Override
    public void setAutoCompletions(){
      LineReader reader = IConsoleReader.getReader();
        subCommands.forEach((s, subCommandExecutor) -> {
            if(subCommandExecutor instanceof SubCommandCompletor){
                SubCommandCompletor completor = (SubCommandCompletor) subCommandExecutor;

                for(Object subs: completor.sub){
                    Console.print(subs, Level.FINE);
                    List<Completer> completors = new ArrayList<>();
                    boolean nullComp = false;
                    /*for(String sub : subs){
                        completors.add(new StringsCompleter(sub));
                    }*/



                   // AggregateCompleter aggregateCompleter = new AggregateCompleter(completors);
                   // CustomArgumentCompleter argumentCompleter = new CustomArgumentCompleter(completors);


                     //   argumentCompleter.setStrict(true);
                    //reader.addCompleter(argumentCompleter);
                }
            }
        });
    }
    @Override
    public void setCompletionsTest(Completer completer){
        //ConsoleReader.sReader.addCompleter(completer);
    }
    @Override
    public void sendHelp(){
        if(helpBuilder.getSize() <= 1){
            Console.printLang("api.commands.invalidArguments");
        }else {
            helpBuilder.build();
        }
    }
    public Command(String name){
        super(name);
    }




    @Override
    public void addSubCommand(String subCommand, SubCommandExecutor sce){
        subCommands.put(subCommand,sce);
    }

    @Override
    public SubCommandExecutor getSubCommand(String subCommand){
        return subCommands.get(subCommand);
    }

    @Override
    public boolean onCommand(String[] args){
       if(commandExecutor.execute(args))
           return true;
       return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HelpBuilder getHelpBuilder() {
        return helpBuilder;
    }


}

