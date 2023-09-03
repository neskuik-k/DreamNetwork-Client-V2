package be.alexandre01.dreamnetwork.api.events.list.screens;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreScreenDestroyEvent extends Event {
    private final DNCoreAPI dnCoreAPI;
    private final IScreen screen;
}
