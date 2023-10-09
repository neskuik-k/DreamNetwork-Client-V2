package be.alexandre01.dreamnetwork.api.commands.sub;

import be.alexandre01.dreamnetwork.api.commands.Command;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand extends SubCommandCompletor implements SubCommandExecutor {
    // EXTENDS

    SubCommandExecutor subCommandExecutor;

    NodeBuilder nodeBuilder;

    List<NodeContainer> colorNodes = new ArrayList<>();

    public SubCommand() {
        super();
    }
    public SubCommand(Command command) {
        super(command);
    }
    // wArgs = Working Args
    public boolean when(SubCommandExecutor subCommandExecutor,String[] args,String... wArgs) {
        return new SubCommandExecutor() {
            @Override
            public boolean onSubCommand(@NonNull String[] subArgs) {
                    int size = 0;
                    for(int i = 0;i<wArgs.length;i++){
                        if(!wArgs[i].startsWith("[") && !wArgs[i].endsWith("]")){
                            size++;
                        }
                    }

                    if(args.length < size){
                        return false;
                    }

                    //Get args
                    return subCommandExecutor.onSubCommand(subArgs);

                //return true;
            }
        }.onSubCommand(args);
    }

    public NodeContainer registerColor(NodeContainer container){
        colorNodes.add(container);
        return container;
    }

    public void registerNode(NodeContainer container){
        nodeBuilder = new NodeBuilder(container);
    }


    public void fail(String... args){
        System.out.println("Wrong usage: "+String.join(" ",args));
    }
}
