package be.alexandre01.dreamnetwork.api.events.list.services;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreServiceLinkedEvent extends Event {
    private final DNCoreAPI dnCoreAPI;
    private final AServiceClient iClient;
    private final IService service;
}
