package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.service.IConfig;

public interface IProfiles {
    void save();

    java.util.HashMap<String, IConfig> getProfiles();
}
