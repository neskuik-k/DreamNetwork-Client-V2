package be.alexandre01.dreamnetwork.api.events.list.services;

import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.addons.Addon;
import be.alexandre01.dreamnetwork.api.events.Cancellable;
import be.alexandre01.dreamnetwork.api.events.Event;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import lombok.Getter;

@Getter
public class CoreServicePreProcessEvent extends Event  {
    private final DNClientAPI dnClientAPI;
    private final IConfig iConfig;

    private String customArguments = null;

    boolean cancelled = false;

    Addon cancelledBy = null;

    public CoreServicePreProcessEvent(DNClientAPI dnClientAPI, IConfig iConfig) {
        this.dnClientAPI = dnClientAPI;
        this.iConfig = iConfig;
    }

    public void addArguments(String arguments) {
        if(customArguments != null) {
            customArguments += " " + arguments;
        } else {
            customArguments = arguments;
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }


    public void setCancelled(boolean cancelled, Addon addon) {
        this.cancelled = cancelled;
        cancelledBy = addon;
    }
}
