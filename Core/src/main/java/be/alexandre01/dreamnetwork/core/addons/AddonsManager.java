package be.alexandre01.dreamnetwork.core.addons;

import be.alexandre01.dreamnetwork.api.addons.DreamExtension;
import be.alexandre01.dreamnetwork.core.Core;
import lombok.Getter;

import java.util.LinkedHashMap;

public class AddonsManager {
    private Core core;

    public AddonsManager(Core core) {
        this.core = core;
    }
    @Getter private LinkedHashMap<String, DreamExtension> addons = new LinkedHashMap<>();

    public void registerAddon(DreamExtension addon) {
        addons.put(addon.getAddon().getDreamyName(), addon);
    }
}
