package be.alexandre01.dreamnetwork.client.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import lombok.NonNull;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Remove extends SubCommandCompletor implements SubCommandExecutor {
    public Remove(){
        setCompletion(node("bundles",
                node("remove")));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args[0].equalsIgnoreCase("remove")){
            return true;
        }
        return false;
    }
}
