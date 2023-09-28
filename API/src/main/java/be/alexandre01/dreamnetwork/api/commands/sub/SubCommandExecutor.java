package be.alexandre01.dreamnetwork.api.commands.sub;


import lombok.NonNull;

public interface SubCommandExecutor {

    boolean onSubCommand(@NonNull String[] args);

}
