package be.alexandre01.dreamnetwork.api.events.list.services;

import be.alexandre01.dreamnetwork.api.DNClientAPI;
import be.alexandre01.dreamnetwork.api.events.Event;
import be.alexandre01.dreamnetwork.api.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CoreServiceStopEvent extends Event {
    private final DNClientAPI dnClientAPI;
    private final IService service;
}
