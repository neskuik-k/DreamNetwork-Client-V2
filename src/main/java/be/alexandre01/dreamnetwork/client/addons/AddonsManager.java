package be.alexandre01.dreamnetwork.client.addons;

import be.alexandre01.dreamnetwork.api.DreamAddon;
import be.alexandre01.dreamnetwork.client.Client;

import java.util.LinkedHashMap;

public class AddonsManager {
    private Client client;

    public AddonsManager(Client client) {
        this.client = client;
    }
    private LinkedHashMap<String, DreamAddon> addons = new LinkedHashMap<>();

    public void registerAddon(DreamAddon addon) {
        addons.put(addon.getName(), addon);
    }
}
