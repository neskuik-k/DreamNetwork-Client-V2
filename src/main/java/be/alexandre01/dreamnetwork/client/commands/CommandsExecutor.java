package be.alexandre01.dreamnetwork.client.commands;


import lombok.NonNull;

public interface CommandsExecutor {
    boolean onCommand(@NonNull String[] args);
}
