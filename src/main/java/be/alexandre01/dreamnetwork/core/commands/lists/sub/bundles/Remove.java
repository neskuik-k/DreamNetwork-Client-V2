package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.core.console.jline.completors.CustomTreeCompleter.node;

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
