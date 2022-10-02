package be.alexandre01.dreamnetwork.api.events.list.commands;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.events.Cancellable;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.Getter;

@Getter
public class CoreCommandExecuteEvent extends Event implements Cancellable {
    private boolean cancelled;
    private String[] args;
    private final DNCoreAPI dnCoreAPI;

    public CoreCommandExecuteEvent(DNCoreAPI dnCoreAPI, String[] args) {
        this.dnCoreAPI = dnCoreAPI;
        this.args = args;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
