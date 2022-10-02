package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import lombok.NonNull;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Create extends SubCommandCompletor implements SubCommandExecutor {
    public Create(){
        setCompletion(node("bundles",
                node("create")));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 3){

            }
            return true;
        }
        return false;
    }
}
