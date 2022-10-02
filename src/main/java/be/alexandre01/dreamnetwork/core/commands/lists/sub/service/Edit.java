package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import lombok.NonNull;

public class Edit extends SubCommandCompletor implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        return false;
    }
}
