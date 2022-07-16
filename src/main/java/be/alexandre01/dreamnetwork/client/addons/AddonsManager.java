package be.alexandre01.dreamnetwork.client.addons;

import be.alexandre01.dreamnetwork.api.addons.DreamExtension;
import be.alexandre01.dreamnetwork.client.Client;
import lombok.Getter;

import java.util.LinkedHashMap;

public class AddonsManager {
    private Client client;

    public AddonsManager(Client client) {
        this.client = client;
    }
    @Getter private LinkedHashMap<String, DreamExtension> addons = new LinkedHashMap<>();

    public void registerAddon(DreamExtension addon) {
        addons.put(addon.getName(), addon);
    }
}
