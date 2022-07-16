package be.alexandre01.dreamnetwork.api.events.list.commands;

import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.events.Cancellable;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.Getter;

@Getter
public class CoreCommandExecuteEvent extends Event implements Cancellable {
    private boolean cancelled;
    private String[] args;
    private final DNClientAPI dnClientAPI;

    public CoreCommandExecuteEvent(DNClientAPI dnClientAPI,String[] args) {
        this.dnClientAPI = dnClientAPI;
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
