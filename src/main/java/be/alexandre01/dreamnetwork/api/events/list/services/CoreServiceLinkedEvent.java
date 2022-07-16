package be.alexandre01.dreamnetwork.api.events.list.services;

import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreServiceLinkedEvent extends Event {
    private final DNClientAPI dnClientAPI;
    private final IClient iClient;
    private final IService service;
}
