package be.alexandre01.dreamnetwork.client.commands;


import lombok.NonNull;

public interface SubCommandExecutor {

    boolean onSubCommand(@NonNull String[] args);
}
