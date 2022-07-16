package be.alexandre01.dreamnetwork.api.events.list;

import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.events.Event;
import be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreInitEvent extends Event {
    private final DNClientAPI dnClientAPI;

}
