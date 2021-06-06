package be.alexandre01.dreamnetwork.client.commands.sub;


import lombok.NonNull;

public interface SubCommandExecutor {

    boolean onSubCommand(@NonNull String[] args);

}
