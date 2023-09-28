package be.alexandre01.dreamnetwork.api.events.list;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreInitEvent extends Event {
    private final DNCoreAPI dnCoreAPI;

}
